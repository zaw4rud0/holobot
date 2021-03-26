package com.xharlock.otakusenpai.games;

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
import com.xharlock.otakusenpai.core.Main;
import com.xharlock.otakusenpai.misc.Emojis;
import com.xharlock.otakusenpai.misc.Emotes;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

// TODO CLEAN EVERYTHING UP

public class AkinatorCmd extends Command {

	private boolean busy;

	private EventWaiter waiter;
	private Akiwrapper akinator;
	private Guess final_guess;
	AtomicInteger counter;

	// TODO If a person gets rejected because someone is already playing, their
	// cooldown gets reset xD

	// TODO Better continue emote

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
		setCommandCategory(CommandCategory.MISC);
		this.waiter = waiter;
		this.busy = false;
		counter = new AtomicInteger();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setThumbnail(Akinator.DEFAULT.getUrl());

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
		counter.set(0);
		
		builder.setTitle("Akinator");
		builder.setDescription(
				"To start the game, please think about a real or fictional character. I will try to guess who it is by asking some questions."
						+ "\nIf you are ready, please react with a tick, or if you want to cancel the game, react with a cross.");

		if (e.isFromGuild()) {
			builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", e.getMember().getEffectiveName()),
					e.getAuthor().getEffectiveAvatarUrl());
			builder.setColor(getGuildColor(e.getGuild()));
		} else {
			Main.otakuSenpai.getConfig().getColor();
		}

		Message msg = e.getChannel().sendMessage(builder.build()).complete();

		msg.addReaction(Emotes.TICK.getAsReaction()).queue();
		msg.addReaction(Emotes.CROSS.getAsReaction()).queue();

		waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
				if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.TICK.getAsReaction())) {
					evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
					msg.clearReactions().queue();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					return true;
				}
				if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
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
				start(e, msg, builder);
			} else if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
				cancel(e, msg, builder);
			} else
				errorEmbed(e, msg);
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
			busy = false;
			akinator = null;
			final_guess = null;
		});
	}

	private void start(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		builder.setThumbnail(Akinator.START.getUrl());
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

	private void cancel(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		builder.setThumbnail(Akinator.CANCEL.getUrl());
		builder.setDescription(e.getAuthor().getAsMention() + " cancelled the game.\nSee you soon!");
		builder.clearFields();
		msg.editMessage(builder.build()).queue();
		msg.delete().queueAfter(15, TimeUnit.SECONDS);
		busy = false;
		akinator = null;
		final_guess = null;
	}

	private void askQuestion(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
				if (evt.getReactionEmote().isEmoji()) {
					if (evt.getReactionEmote().getEmoji().equals("1\u20e3")) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						akinator.answerCurrentQuestion(Answer.YES);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("2\u20e3")) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						akinator.answerCurrentQuestion(Answer.NO);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("3\u20e3")) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						akinator.answerCurrentQuestion(Answer.DONT_KNOW);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("4\u20e3")) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						akinator.answerCurrentQuestion(Answer.PROBABLY);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("5\u20e3")) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						akinator.answerCurrentQuestion(Answer.PROBABLY_NOT);
						return true;
					}
				} else {
					if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.UNDO.getAsReaction())) {
						evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
						if (counter.get() > 1) {
							akinator.undoAnswer();
							return true;
						} else {
							return false;
						}
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
				builder.setDescription(
						"**Q" + counter.decrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
				msg.editMessage(builder.build()).queue();
				askQuestion(e, msg, builder);
			}
			// Cancel
			else if (!evt.getReactionEmote().isEmoji()
					&& evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
				cancel(e, msg, builder);
			} else {
				for (Guess guess : akinator.getGuesses()) {
					if (guess.getProbability() >= 0.9) {
						final_guess = guess;
						guess(e, msg, builder);
						return;
					}
				}
				builder.setThumbnail(getRandomThinking());
				builder.setDescription(
						"**Q" + counter.incrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
				msg.editMessage(builder.build()).queue();
				askQuestion(e, msg, builder);
			}
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
			busy = false;
			akinator = null;
			final_guess = null;
		});

	}

	private void guess(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		msg.clearReactions().queue();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		msg.addReaction(Emotes.TICK.getAsReaction()).queue();		
		msg.addReaction(Emotes.CROSS.getAsReaction()).queue();
		msg.addReaction(Emotes.CONTINUE.getAsReaction()).queue();
		
		builder.setTitle("Akinator");
		builder.setThumbnail(Akinator.GUESSING.getUrl());
		builder.clearFields();

		if (final_guess.getDescription() == null || final_guess.getDescription().equals("null")) {
			builder.setDescription("Your character is: " + final_guess.getName());
		} else
			builder.setDescription("Your character is: " + final_guess.getName() + "\n" + final_guess.getDescription());
		if (final_guess.getImage() != null)
			builder.setImage(final_guess.getImage().toString());

		msg.editMessage(builder.build()).queue();

		waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {

				if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.TICK.getAsReaction())) {
					evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
					msg.clearReactions().queue();
					return true;
				}
				if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
					evt.getReaction().removeReaction(evt.retrieveUser().complete()).queue();
					msg.clearReactions().queue();
					return true;
				}
				if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CONTINUE.getAsReaction())) {
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
				victory(e, msg, builder);
			} else if (evt.getReactionEmote().getAsReactionCode().equals(Emotes.CROSS.getAsReaction())) {
				defeat(e, msg, builder);
			} else {
				EmbedBuilder builder_new = new EmbedBuilder();
				builder_new.setTitle("Akinator");
				builder_new.setThumbnail(getRandomThinking());
				builder_new.setDescription(
						"**Q" + counter.incrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
				builder_new.addField("Answers",
						":one: Yes\n" + ":two: No\n" + ":three: I don't know\n" + ":four: Probably\n" + ":five: Probably not",
						false);
				builder_new.addField("Other",
						Emotes.UNDO.getAsText() + " Undo last answer\n" + Emotes.CROSS.getAsText() + " Cancel game", false);
				msg.editMessage(builder_new.build()).queue();
				addInGameReactions(msg);
				askQuestion(e, msg, builder);
			}
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
			busy = false;
			akinator = null;
			final_guess = null;
		});
	}

	// What happens if Akinator wins
	private void victory(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {

		
		
		busy = false;
		akinator = null;
		final_guess = null;
	}

	// What happens if Akinator loses
	private void defeat(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {

		msg.clearReactions().queue();
		
		builder = new EmbedBuilder();
		
		
		busy = false;
		akinator = null;
		final_guess = null;
	}
	
	private void questionEmbed(MessageReceivedEvent e, Message msg) {
				
	}

	private void errorEmbed(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(Messages.TITLE_ERROR.getText());
		builder.setDescription("Something went wrong");
		sendEmbed(e, builder, 15, TimeUnit.MINUTES, false);
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
	
	public String getRandomThinking() {		
		Random rand = new Random();
		int index = rand.nextInt(7);		
		if (index == 0)
			return Akinator.THINKING_1.getUrl();
		else if (index == 1)
			return Akinator.THINKING_2.getUrl();
		else if (index == 2)
			return Akinator.THINKING_3.getUrl();
		else if (index == 3)
			return Akinator.THINKING_4.getUrl();
		else if (index == 4)
			return Akinator.THINKING_5.getUrl();
		else if (index == 5)
			return Akinator.THINKING_6.getUrl();
		else
			return Akinator.START.getUrl();
	}
}
