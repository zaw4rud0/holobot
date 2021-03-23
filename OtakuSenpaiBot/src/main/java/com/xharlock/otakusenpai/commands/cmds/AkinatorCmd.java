package com.xharlock.otakusenpai.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.markozajc.akiwrapper.*;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.misc.Emojis;
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
	
	private String icon_url = "https://media.discordapp.net/attachments/823875581460480010/823875638473392168/unnamed.png";
	
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
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		if (busy) {
			e.getChannel().sendMessage("I'm currently busy, please wait until I'm done!").queue();
			return;
		}
		
		
		try {
			akinator = new AkiwrapperBuilder().build();
		} catch (ServerNotFoundException ex) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setThumbnail(icon_url);
			builder.setTitle(Messages.TITLE_ERROR.getText());
			builder.setDescription("Failed to connect with server. Please try again later!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		busy = true;
		
		question(e);
	}

	private void guessing(MessageReceivedEvent e) {
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(icon_url);		
		builder.setDescription("Your character is: " + final_guess.getName() + "\n"
				+ final_guess.getDescription());
		builder.setImage(final_guess.getImage().toString());
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
		
		busy = false;
		akinator = null;
		final_guess = null;
	}

	private void question(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setThumbnail(icon_url);
		
		builder.setDescription(akinator.getCurrentQuestion().getQuestion());
		builder.addField("Answers", ":one: Yes\n" + ":two: No\n" + ":three: I don't know\n" + ":four: Probably\n" + ":five: Probably not", false);
		
		e.getChannel().sendMessage(builder.build()).queue(msg -> {
			addReactions(msg);
			
			waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {				
				if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {
					
					if (evt.getReactionEmote().getEmoji().equals("1\u20e3")) {
						akinator.answerCurrentQuestion(Answer.YES);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("2\u20e3")) {
						akinator.answerCurrentQuestion(Answer.NO);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("3\u20e3")) {
						akinator.answerCurrentQuestion(Answer.DONT_KNOW);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("4\u20e3")) {
						akinator.answerCurrentQuestion(Answer.PROBABLY);
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals("5\u20e3")) {
						akinator.answerCurrentQuestion(Answer.PROBABLY_NOT);
						return true;
					}
				}
				return false;
			}, evt -> {
				msg.delete().queue();
				for (Guess guess : akinator.getGuesses()) {
					if (guess.getProbability() >= 0.9) {						
						final_guess = guess;
						guessing(e);
						return;
					}
				}				
				question(e);
			}, 5, TimeUnit.MINUTES, () -> {
				msg.delete().queue();
			});
		});
	}
	
	private void addReactions(Message msg) {
		msg.addReaction(Emojis.ONE.getAsReaction()).queue();
		msg.addReaction(Emojis.TWO.getAsReaction()).queue();
		msg.addReaction(Emojis.THREE.getAsReaction()).queue();
		msg.addReaction(Emojis.FOUR.getAsReaction()).queue();
		msg.addReaction(Emojis.FIVE.getAsReaction()).queue();
	}
}
