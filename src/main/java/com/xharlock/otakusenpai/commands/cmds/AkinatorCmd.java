package com.xharlock.otakusenpai.commands.cmds;

import java.util.List;
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
import com.xharlock.otakusenpai.misc.Emojis;
import com.xharlock.otakusenpai.misc.Emotes;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class AkinatorCmd extends Command {

	private boolean busy;

	private EventWaiter waiter;
	private Akiwrapper akinator;
	private Guess final_guess;
	
	AtomicInteger counter;

	private String icon_url = "https://media.discordapp.net/attachments/823875581460480010/823875638473392168/unnamed.png";

	// TODO If a person gets rejected because someone is already playing, their
	// cooldown gets reset xD

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
		setCommandCategory(CommandCategory.MISC);
		this.waiter = waiter;
		this.busy = false;
		counter = new AtomicInteger();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setThumbnail(icon_url);

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
		builder.setThumbnail(icon_url);
		builder.setDescription(
				"To start the game, please think about a real or fictional character. I will try to guess who it is by asking some questions."
						+ "\nIf you are ready, please react with a tick, or if you want to cancel the game, react with a cross.");

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
				error(e, msg);
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
		});
	}

	private void start(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		builder.setDescription("**Q" + counter.incrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
		builder.addField("Answers",
				":one: Yes\n" + ":two: No\n" + ":three: I don't know\n" + ":four: Probably\n" + ":five: Probably not",
				false);		
		msg.editMessage(builder.build()).queue();		
		addAnswerReactions(msg);		
		askQuestion(e, msg, builder);
	}

	private void cancel(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {

		return;
	}

	private void victory(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {

		return;
	}

	private void askQuestion(MessageReceivedEvent e, Message msg, EmbedBuilder builder) {
		waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
			if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
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
			}
			return false;
		}, evt -> {
			for (Guess guess : akinator.getGuesses()) {
				if (guess.getProbability() >= 0.9) {
					final_guess = guess;
					guess(e, msg);
					return;
				}
			}
			builder.setDescription("**Q" + counter.incrementAndGet() + ":** " + akinator.getCurrentQuestion().getQuestion());
			msg.editMessage(builder.build()).queue();
			askQuestion(e, msg, builder);
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
		});

	}

	private void guess(MessageReceivedEvent e, Message msg) {
		
		msg.clearReactions().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(icon_url);

		if (final_guess.getDescription().equals("null")) {
			builder.setDescription("Your character is: " + final_guess.getName());
		} else
			builder.setDescription("Your character is: " + final_guess.getName() + "\n" + final_guess.getDescription());

		if (final_guess.getImage() != null)
			builder.setImage(final_guess.getImage().toString());

		msg.editMessage(builder.build()).queue();

		busy = false;
		akinator = null;
		final_guess = null;
	}

	private void error(MessageReceivedEvent e, Message msg) {
		msg.clearReactions().queue();

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(Messages.TITLE_ERROR.getText());
		builder.setDescription("Something went wrong");
		sendEmbed(e, builder, 15, TimeUnit.MINUTES, false);
	}

	private void addAnswerReactions(Message msg) {
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
	}
}
