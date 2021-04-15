package com.xharlock.otakusenpai.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.core.Bootstrap;
import com.xharlock.otakusenpai.misc.Emojis;
import com.xharlock.otakusenpai.misc.Emotes;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class AkinatorCmd extends Command {

	private EventWaiter waiter;
	private Akiwrapper akinator;
	private boolean busy;

	private double probability = 0.65;
	
	private Reaction[] reactions;
	private AtomicInteger counter;
	private List<String> wrong;

	public AkinatorCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to start a new game of Akinator."
				+ "\nIn a nutshell, you have to think of a character, real or fictional, and answer the "
				+ "questions by using the according reaction. "
				+ "Possible answers are 'yes', 'no', 'don't know', 'probably' and 'probably not'."
				+ "After some questions Akinator will try to guess your character.");
		setUsage(name);
		setAliases(List.of());
		setCmdCooldown(300);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GAMES);

		this.waiter = waiter;
		this.busy = false;
		counter = new AtomicInteger();
		wrong = new ArrayList<>();

		this.reactions = new Reaction[] { new Reaction("1\u20e3", Answer.YES), new Reaction("2\u20e3", Answer.NO),
				new Reaction("3\u20e3", Answer.DONT_KNOW), new Reaction("4\u20e3", Answer.PROBABLY),
				new Reaction("5\u20e3", Answer.PROBABLY_NOT) };
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setThumbnail(AkinatorSprites.DEFAULT.getUrl());
		if (busy) {
			builder.setTitle(Messages.TITLE_BUSY.getText());
			builder.setDescription("I'm currently busy, please wait until I'm done!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		try {
			akinator = new AkiwrapperBuilder().build();
		} catch (ServerNotFoundException ex) {
			builder.setTitle(Messages.TITLE_ERROR.getText());
			builder.setDescription("Failed to connect. Please try again later!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		busy = true;
		start(e);
	}

	private void start(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSprites.DEFAULT.getUrl());
		builder.setColor(Bootstrap.otakuSenpai.getConfig().getColor());
		builder.setDescription(
				"To start the game, please think about a real or fictional character. I will try to guess who it is by asking some questions."
						+ "\nIf you are ready, please react with " + Emotes.TICK.getAsText() + ", or if you want to cancel the game, react with " + Emotes.CROSS.getAsText() + ".");
		builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", e.getMember().getEffectiveName()),
				e.getAuthor().getEffectiveAvatarUrl());

		Message msg = e.getChannel().sendMessage(builder.build()).complete();
		addStartReactions(msg);

		waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {			
			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
				if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.TICK.getAsReaction())
						|| evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
					evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
					msg.clearReactions().queue();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					return true;
				}
			}
			return false;
		}, evt -> {
			if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.TICK.getAsReaction())) {
				inGame(e, msg);
			} else if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
				cancel(e, msg);
			} else
				error(e, msg);
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
			cleanup();
		});

	}

	private void inGame(MessageReceivedEvent e, Message msg) {
		
		// Check if Akinator has run out of questions
		if (akinator.getCurrentQuestion() == null) {
			defeat(e, msg);
			return;			
		}
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setColor(Bootstrap.otakuSenpai.getConfig().getColor());
		builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", e.getMember().getEffectiveName()),
				e.getAuthor().getEffectiveAvatarUrl());
		builder.setThumbnail(AkinatorSprites.START.getUrl());
		builder.setDescription(
				"**Q" + counter.incrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
		builder.addField("Answers",
				":one: Yes\n" + ":two: No\n" + ":three: I don't know\n" + ":four: Probably\n" + ":five: Probably not",
				false);
		builder.addField("Other",
				Emotes.UNDO.getAsText() + " Undo last answer\n" + Emotes.CROSS.getAsText() + " Cancel game", false);
		msg.editMessage(builder.build()).queue();
		addInGameReactions(msg);
		askQuestion(e, msg, builder);
	}

	private void askQuestion(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
				if (evt.getReactionEmote().isEmoji()) {
					for (int i = 0; i < 5; i++) {
						Reaction r = reactions[i];
						if (evt.getReactionEmote().getEmoji().equals(r.emote)) {
							evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
							akinator.answerCurrentQuestion(r.answer);
							return true;
						}
					}
				} else {
					if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.UNDO.getAsReaction())) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						if (counter.get() > 1) {
							akinator.undoAnswer();
							return true;
						} else
							return false;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						msg.clearReactions().queue();
						return true;
					}
				}
			}
			return false;
		}, evt -> {
			// Undo
			if (!evt.getReactionEmote().isEmoji()
					&& evt.getReactionEmote().getAsReactionCode().equals(Emotes.UNDO.getAsReaction())) {
				builder.setThumbnail(getRandomThinking());
				builder.setDescription("**Q" + counter.decrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
				msg.editMessage(builder.build()).queue();
				askQuestion(e, msg, builder);
			}
			// Cancel
			else if (!evt.getReactionEmote().isEmoji()
					&& evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
				cancel(e, msg);
			}
			// Any answer
			else {
				// Akinator has some guesses
				if (akinator.getGuessesAboveProbability(probability).size() != 0) {
					Guess max = null;
					for (Guess guess : akinator.getGuessesAboveProbability(probability)) {
						if (wrong.contains(guess.getName())) {
							continue;
						}
						if (max == null) {
							max = guess;
							continue;
						}
						if (guess.getProbability() > max.getProbability()) {
							max = guess;
						}
					}

					if (max != null) {
						guess(e, msg, max);
						return;
					}
				}
				// Check if Akinator has run out of questions
				if (akinator.getCurrentQuestion() == null) {
					defeat(e, msg);
					return;
				}
				
				builder.setThumbnail(getRandomThinking());
				builder.setDescription(
						"**Q" + counter.incrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
				msg.editMessage(builder.build()).queue();
				askQuestion(e, msg, builder);
			}
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
			cleanup();
		});
	}

	/**
	 * Displays his most probable character guess
	 * 
	 * @param e     = MessageReceivedEvent
	 * @param msg   = Message
	 * @param guess = Character
	 */
	private void guess(MessageReceivedEvent e, Message msg, Guess guess) {
		msg.clearReactions().queue();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		addGuessReactions(msg);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setColor(Bootstrap.otakuSenpai.getConfig().getColor());
		builder.setThumbnail(AkinatorSprites.GUESSING.getUrl());
		builder.setFooter(e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
		
		if (guess.getDescription() == null || guess.getDescription().equals("null")) {
			builder.setDescription("Your character is: " + guess.getName());
		} else
			builder.setDescription("Your character is: " + guess.getName() + "\n" + guess.getDescription());
		if (guess.getImage() != null)
			builder.setImage(guess.getImage().toString());

		builder.addField("Answers", Emotes.TICK.getAsText() + " Correct, that was my character!\n" + 
				Emotes.CONTINUE.getAsText() + " Wrong, continue game\n" + Emotes.CROSS.getAsText() + "Cancel game", false);
		
		msg.editMessage(builder.build()).queue();

		waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
				if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.TICK.getAsReaction())
						|| evt.getReactionEmote().getAsReactionCode().equals(Emotes.CONTINUE.getAsReaction())
						|| evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
					evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
					msg.clearReactions().queue();
					return true;
				}
			}
			return false;
		}, evt -> {
			if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.TICK.getAsReaction())) {
				victory(e, msg, guess);
			} else if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
				cancel(e, msg);
			} else {
				wrong.add(guess.getName());
				inGame(e, msg);
			}
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
			cleanup();
		});
	}

	// What happens if Akinator wins
	private void victory(MessageReceivedEvent e, Message msg, Guess right) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSprites.VICTORY.getUrl());
		builder.setColor(Bootstrap.otakuSenpai.getConfig().getColor());
		builder.setDescription("Great, guessed right one more time!\n"
				+ "It took me `" + counter.get() + "` questions to correctly guess " + right.getName());
		if (right.getImage() != null)
			builder.setImage(right.getImage().toString());
		builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", e.getMember().getEffectiveName()),
				e.getAuthor().getEffectiveAvatarUrl());
		msg.editMessage(builder.build()).queue();
		cleanup();
	}

	// What happens if Akinator loses
	private void defeat(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSprites.DEFEAT.getUrl());
		builder.setColor(Bootstrap.otakuSenpai.getConfig().getColor());
		builder.setDescription("Congratulations " + e.getAuthor().getAsMention() + ", you managed to defeat me!");
		builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", e.getMember().getEffectiveName()),
				e.getAuthor().getEffectiveAvatarUrl());
		msg.editMessage(builder.build()).queue();
		cleanup();
	}

	// User cancels the game
	private void cancel(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setColor(Bootstrap.otakuSenpai.getConfig().getColor());
		builder.setThumbnail(AkinatorSprites.CANCEL.getUrl());
		builder.setDescription(e.getAuthor().getAsMention() + " cancelled the game.\nSee you soon!");
		builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", e.getMember().getEffectiveName()),
				e.getAuthor().getEffectiveAvatarUrl());
		msg.editMessage(builder.build()).queue();
		msg.delete().queueAfter(15, TimeUnit.SECONDS);
		cleanup();
	}

	private void cleanup() {
		this.akinator = null;
		this.busy = false;
		this.wrong.clear();
		this.counter.set(0);
	}

	private void error(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(Messages.TITLE_ERROR.getText());
		builder.setDescription("Something went wrong");
		sendEmbed(e, builder, 15, TimeUnit.MINUTES, false);
	}

	private void addStartReactions(Message msg) {
		msg.addReaction(Emotes.TICK.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emotes.CROSS.getAsReaction()).queue(v -> {
		}, err -> {
		});
	}

	private void addInGameReactions(Message msg) {
		msg.addReaction(Emojis.ONE.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emojis.TWO.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emojis.THREE.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emojis.FOUR.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emojis.FIVE.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emotes.UNDO.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emotes.CROSS.getAsReaction()).queue(v -> {
		}, err -> {
		});
	}

	private void addGuessReactions(Message msg) {
		msg.addReaction(Emotes.TICK.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emotes.CONTINUE.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emotes.CROSS.getAsReaction()).queue(v -> {
		}, err -> {
		});
	}

	public String getRandomThinking() {
		Random rand = new Random();
		int index = rand.nextInt(7);
		if (index == 0)
			return AkinatorSprites.THINKING_1.getUrl();
		else if (index == 1)
			return AkinatorSprites.THINKING_2.getUrl();
		else if (index == 2)
			return AkinatorSprites.THINKING_3.getUrl();
		else if (index == 3)
			return AkinatorSprites.THINKING_4.getUrl();
		else if (index == 4)
			return AkinatorSprites.THINKING_5.getUrl();
		else if (index == 5)
			return AkinatorSprites.THINKING_6.getUrl();
		else
			return AkinatorSprites.START.getUrl();
	}
}

class Reaction {
	public String emote;
	public Answer answer;

	public Reaction(String emote, Answer answer) {
		this.emote = emote;
		this.answer = answer;
	}
}
