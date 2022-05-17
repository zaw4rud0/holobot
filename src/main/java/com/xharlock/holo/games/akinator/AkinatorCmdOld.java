package com.xharlock.holo.games.akinator;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.EmbedColor;
import com.xharlock.holo.misc.Emoji;
import com.xharlock.holo.misc.Emote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Deprecated
@Command(name = "akinator",
		description = "Use this command to start a new game of Akinator." + "\nIn a nutshell, you have to think of a character, real or fictional, and answer the "
		+ "questions by using the according reaction. Possible answers are 'yes', 'no', 'don't know', 'probably' and 'probably not'. After some questions "
		+ "Akinator will try to guess your character.",
		thumbnail = AkinatorSprite.DEFAULT,
		embedColor = EmbedColor.AKINATOR,
		category = CommandCategory.GAMES)
public class AkinatorCmdOld extends AbstractCommand {

	private final EventWaiter waiter;
	private Akiwrapper akinator;
	private boolean busy;

	private final double probability = 0.65;

	private final Reaction[] reactions;
	private final List<String> wrong;

	public AkinatorCmdOld(EventWaiter waiter) {
		this.waiter = waiter;
		busy = false;
		wrong = new ArrayList<>();

		reactions = new Reaction[] { new Reaction(Emoji.ONE.getAsDisplay(), Answer.YES), new Reaction(Emoji.TWO.getAsDisplay(), Answer.NO), new Reaction(Emoji.THREE.getAsDisplay(), Answer.DONT_KNOW),
				new Reaction(Emoji.FOUR.getAsDisplay(), Answer.PROBABLY), new Reaction(Emoji.FIVE.getAsDisplay(), Answer.PROBABLY_NOT) };
	}

	// TODO Clean up

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();

		builder.setThumbnail(AkinatorSpriteOld.DEFAULT.getUrl());

		if (busy) {
			builder.setTitle("Busy");
			builder.setDescription("I'm currently busy, please wait until I'm done!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true, getEmbedColor());
			return;
		}

		try {
			akinator = new AkiwrapperBuilder().build();
		} catch (ServerNotFoundException ex) {
			builder.setTitle("Error");
			builder.setDescription("Failed to connect. Please try again later!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, getEmbedColor());
			return;
		}
		busy = true;
		start(e);
	}

	private void start(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSpriteOld.DEFAULT.getUrl());
		builder.setColor(getEmbedColor());
		builder.setDescription("To start the game, please think about a real or fictional character. I will try to guess who it is by asking some questions." + "\nIf you are ready, please react with "
				+ Emote.TICK.getAsText() + ", or if you want to cancel the game, react with " + Emote.CROSS.getAsText() + ".");

		Message msg = e.getChannel().sendMessageEmbeds(builder.build()).complete();
		addStartReactions(msg);

		waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {

			// So reactions on other messages are ignored
			if (evt.getMessageIdLong() != msg.getIdLong()) {
				return false;
			}

			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
				if (evt.getReactionEmote().getAsReactionCode().equals(Emote.TICK.getAsReaction()) || evt.getReactionEmote().getAsReactionCode().equals(Emote.CROSS.getAsReaction())) {
					evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
					msg.clearReactions().queue();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					return true;
				}
			}
			return false;
		}, evt -> {
			if (evt.getReactionEmote().getAsReactionCode().equals(Emote.TICK.getAsReaction())) {
				inGame(e, msg);
			} else if (evt.getReactionEmote().getAsReactionCode().equals(Emote.CROSS.getAsReaction())) {
				cancel(e, msg);
			} else {
				error(e, msg);
			}
		}, 2, TimeUnit.MINUTES, () -> {
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
		builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		builder.setThumbnail(AkinatorSpriteOld.START.getUrl());
		builder.setColor(getEmbedColor());
		builder.setDescription("**Q" + (akinator.getCurrentQuestion().getStep() + 1) + ":** " + akinator.getCurrentQuestion().getQuestion());
		builder.addField("Answers", ":one: Yes\n" + ":two: No\n" + ":three: I don't know\n" + ":four: Probably\n" + ":five: Probably not", false);
		builder.addField("Other", Emote.UNDO.getAsText() + " Undo last answer\n" + Emote.CROSS.getAsText() + " Cancel game", false);
		msg.editMessageEmbeds(builder.build()).queue();
		addInGameReactions(msg);
		askQuestion(e, msg, builder);
	}

	private void askQuestion(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {

			// So reactions on other messages are ignored
			if (evt.getMessageIdLong() != msg.getIdLong()) {
				return false;
			}

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
					if (evt.getReactionEmote().getAsReactionCode().equals(Emote.UNDO.getAsReaction())) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						if (akinator.getCurrentQuestion().getStep() > 1) {
							akinator.undoAnswer();
							return true;
						} else {
							return false;
						}
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emote.CROSS.getAsReaction())) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						msg.clearReactions().queue();
						return true;
					}
				}
			}
			return false;
		}, evt -> {
			// Undo
			if (!evt.getReactionEmote().isEmoji() && evt.getReactionEmote().getAsReactionCode().equals(Emote.UNDO.getAsReaction())) {
				builder.setThumbnail(getRandomThinking());
				builder.setDescription("**Q" + (akinator.getCurrentQuestion().getStep() - 1) + ":** " + akinator.getCurrentQuestion().getQuestion());
				msg.editMessageEmbeds(builder.build()).queue();
				askQuestion(e, msg, builder);
			}
			// Cancel
			else if (!evt.getReactionEmote().isEmoji() && evt.getReactionEmote().getAsReactionCode().equals(Emote.CROSS.getAsReaction())) {
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
				builder.setDescription("**Q" + (akinator.getCurrentQuestion().getStep() + 1) + ":** " + akinator.getCurrentQuestion().getQuestion());
				msg.editMessageEmbeds(builder.build()).queue();
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
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		addGuessReactions(msg);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSpriteOld.GUESSING.getUrl());
		builder.setColor(getEmbedColor());
		builder.setFooter(e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());

		if (guess.getDescription() == null || guess.getDescription().equals("null")) {
			builder.setDescription("Your character is: " + guess.getName());
		} else {
			builder.setDescription("Your character is: " + guess.getName() + "\n" + guess.getDescription());
		}

		if (guess.getImage() != null) {
			builder.setImage(guess.getImage().toString());
		}

		builder.addField("Answers", Emote.TICK.getAsText() + " Correct, that was my character!\n" + Emote.CONTINUE.getAsText() + " Wrong, continue game\n" + Emote.CROSS.getAsText() + "Cancel game",
				false);

		msg.editMessageEmbeds(builder.build()).queue();

		waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {

			// So reactions on other messages are ignored
			if (evt.getMessageIdLong() != msg.getIdLong()) {
				return false;
			}

			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
				if (evt.getReactionEmote().getAsReactionCode().equals(Emote.TICK.getAsReaction()) || evt.getReactionEmote().getAsReactionCode().equals(Emote.CONTINUE.getAsReaction())
						|| evt.getReactionEmote().getAsReactionCode().equals(Emote.CROSS.getAsReaction())) {
					evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
					msg.clearReactions().queue();
					return true;
				}
			}
			return false;
		}, evt -> {
			if (evt.getReactionEmote().getAsReactionCode().equals(Emote.TICK.getAsReaction())) {
				victory(e, msg, guess, akinator.getCurrentQuestion().getStep() + 1);
			} else if (evt.getReactionEmote().getAsReactionCode().equals(Emote.CROSS.getAsReaction())) {
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
	private void victory(MessageReceivedEvent e, Message msg, Guess right, int counter) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSpriteOld.VICTORY.getUrl());
		builder.setColor(getEmbedColor());
		builder.setDescription("Great, guessed right one more time!\n" + "It took me `" + counter + "` questions to correctly guess " + right.getName());
		if (right.getImage() != null) {
			builder.setImage(right.getImage().toString());
		}
		builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		msg.editMessageEmbeds(builder.build()).queue();
		cleanup();
	}

	// What happens if Akinator loses
	private void defeat(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSpriteOld.DEFEAT.getUrl());
		builder.setColor(getEmbedColor());
		builder.setDescription("Congratulations " + e.getAuthor().getAsMention() + ", you managed to defeat me!");
		builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		msg.editMessageEmbeds(builder.build()).queue();
		cleanup();
	}

	// User cancels the game
	private void cancel(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(AkinatorSpriteOld.CANCEL.getUrl());
		builder.setColor(getEmbedColor());
		builder.setDescription(e.getAuthor().getAsMention() + " cancelled the game.\nSee you soon!");
		builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		msg.editMessageEmbeds(builder.build()).queue();
		msg.delete().queueAfter(15, TimeUnit.SECONDS);
		cleanup();
	}

	private void cleanup() {
		akinator = null;
		busy = false;
		wrong.clear();
	}

	private void error(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Error");
		builder.setDescription("Something went wrong");
		sendEmbed(e, builder, 15, TimeUnit.MINUTES, false, getEmbedColor());
	}

	private void addStartReactions(Message msg) {
		msg.addReaction(Emote.TICK.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emote.CROSS.getAsReaction()).queue(v -> {
		}, err -> {
		});
	}

	private void addInGameReactions(Message msg) {
		msg.addReaction(Emoji.ONE.getAsDisplay()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emoji.TWO.getAsDisplay()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emoji.THREE.getAsDisplay()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emoji.FOUR.getAsDisplay()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emoji.FIVE.getAsDisplay()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emote.UNDO.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emote.CROSS.getAsReaction()).queue(v -> {
		}, err -> {
		});
	}

	private void addGuessReactions(Message msg) {
		msg.addReaction(Emote.TICK.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emote.CONTINUE.getAsReaction()).queue(v -> {
		}, err -> {
		});
		msg.addReaction(Emote.CROSS.getAsReaction()).queue(v -> {
		}, err -> {
		});
	}

	public String getRandomThinking() {
		Random rand = new Random();
		int index = rand.nextInt(7);

		switch (index) {
		case 0:
			return AkinatorSpriteOld.THINKING_1.getUrl();
		case 1:
			return AkinatorSpriteOld.THINKING_2.getUrl();
		case 2:
			return AkinatorSpriteOld.THINKING_3.getUrl();
		case 3:
			return AkinatorSpriteOld.THINKING_4.getUrl();
		case 4:
			return AkinatorSpriteOld.THINKING_5.getUrl();
		case 5:
			return AkinatorSpriteOld.THINKING_6.getUrl();
		default:
			return AkinatorSpriteOld.START.getUrl();
		}
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

@Deprecated
enum AkinatorSpriteOld {
	DEFAULT("https://media.discordapp.net/attachments/824916413139124254/824917438062919740/akinator_default.png"),
	START("https://media.discordapp.net/attachments/824916413139124254/824927512827527178/akinator_start.png"),
	THINKING_1("https://media.discordapp.net/attachments/824916413139124254/824917445125865472/akinator_thinking_1.png"),
	THINKING_2("https://media.discordapp.net/attachments/824916413139124254/824917445629575168/akinator_thinking_2.png"),
	THINKING_3("https://media.discordapp.net/attachments/824916413139124254/824917447999750144/akinator_thinking_3.png"),
	THINKING_4("https://media.discordapp.net/attachments/824916413139124254/824927627483545610/akinator_thinking_4.png"),
	THINKING_5("https://media.discordapp.net/attachments/824916413139124254/824927631589376010/akinator_thinking_5.png"),
	THINKING_6("https://media.discordapp.net/attachments/824916413139124254/824927633992581130/akinator_thinking_6.png"),
	SHOCKED("https://media.discordapp.net/attachments/824916413139124254/824927453982097418/akinator_shocked.png"),
	DEFEAT("https://media.discordapp.net/attachments/824916413139124254/824917439242043412/akinator_defeat.png"),
	GUESSING("https://media.discordapp.net/attachments/824916413139124254/824917441557299220/akinator_guessing.png"),
	VICTORY("https://media.discordapp.net/attachments/824916413139124254/824917454647721984/akinator_victory.png"),
	CANCEL("https://media.discordapp.net/attachments/824916413139124254/824927268140089415/akinator_cancel.png");

	private final String url;

	AkinatorSpriteOld(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}
