package com.xharlock.holo.games.akinator;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.xharlock.holo.exceptions.APIException;
import com.xharlock.holo.misc.EmbedColor;
import com.xharlock.holo.misc.Emoji;
import com.xharlock.holo.misc.Emote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * An instance of Akinator with its sprites and questions/answers
 */
public class Akinator {
	
	private final Akiwrapper instance;
	private final MessageReceivedEvent ev;
	private final AkinatorManager manager;
	private final EventWaiter waiter;
	/** The message containing the game */
	private Message msg;
	
	public Akinator(MessageReceivedEvent ev, AkinatorManager manager, EventWaiter waiter) throws APIException {
		try {
			instance = new AkiwrapperBuilder().build();
		} catch (ServerNotFoundException e) {
			throw new APIException(e.getMessage());
		}
		
		this.ev = ev;
		this.manager = manager;
		this.waiter = waiter;
	}
	
	public Akinator(MessageReceivedEvent ev, GuessType type, AkinatorManager manager, EventWaiter waiter) throws APIException {
		try {
			instance = new AkiwrapperBuilder()
					.setLanguage(Language.ENGLISH)
					.setGuessType(type)
					.build();
		} catch (ServerNotFoundException e) {
			throw new APIException("Server not found for this language and guess type!");
		}
		
		this.ev = ev;
		this.manager = manager;
		this.waiter = waiter;
	}
	
	public void start() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Akinator");
		builder.setDescription("Welcome to Akinator!\n\n");

		// TODO Extensive description

		builder.setThumbnail(AkinatorSprite.START);
		builder.setColor(EmbedColor.AKINATOR.getColor());

		this.msg = ev.getChannel().sendMessageEmbeds(builder.build()).complete();
		addStartReactions(msg);

		waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {

			// Reactions on other messages are ignored
			if (evt.getMessageIdLong() != msg.getIdLong()) {
				return false;
			}

			if (!evt.retrieveUser().complete().isBot() && ev.getAuthor().equals(evt.retrieveUser().complete())) {
				return true;
			}

			return false;
		}, evt -> {
			if (evt.getReactionEmote().getAsReactionCode().equals(Emote.TICK.getAsReaction())) {

			}
		}, 2, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
			cleanUp();
		});
	}
	
	public void ask() {
		
	}
	
	public void guess() {
		
	}
	
	public void cancel() {
		manager.removeInstance(ev.getAuthor().getIdLong());
	}
	
	private void addStartReactions(Message msg) {
		List<Object> reactions = List.of(Emote.TICK, Emote.CROSS);

		for (Object reaction : reactions) {
			if (reaction instanceof Emote) {
				msg.addReaction(((Emote) reaction).getAsReaction()).queue(v -> {}, err -> {});
			}
		}
	}

	private void addInGameReactions(Message msg) {
		List<Object> reactions = List.of(Emoji.ONE, Emoji.TWO, Emoji.THREE, Emoji.FOUR, Emoji.FIVE, Emote.UNDO, Emote.CROSS);

		for (Object reaction : reactions) {
			if (reaction instanceof Emoji) {
				msg.addReaction(((Emoji) reaction).getAsDisplay()).queue(v -> {}, err -> {});
			} else {
				msg.addReaction(((Emote) reaction).getAsReaction()).queue(v -> {}, err -> {});
			}
		}
	}

	private void addGuessReactions(Message msg) {
		List<Object> reactions = List.of(Emote.TICK, Emote.CONTINUE, Emote.CROSS);

		for (Object reaction : reactions) {
			if (reaction instanceof Emote) {
				msg.addReaction(((Emote) reaction).getAsReaction()).queue(v -> {}, err -> {});
			}
		}
	}

	private void cleanUp() {
		msg.clearReactions().queue();
	}

	public Akiwrapper getAkinator() { return instance; }
	
	private String getRandomThinking() {
		int index = new Random().nextInt(7);
		return switch (index) {
			case 0 -> AkinatorSprite.THINKING_1;
			case 1 -> AkinatorSprite.THINKING_2;
			case 2 -> AkinatorSprite.THINKING_3;
			case 3 -> AkinatorSprite.THINKING_4;
			case 4 -> AkinatorSprite.THINKING_5;
			case 5 -> AkinatorSprite.THINKING_6;
			default -> AkinatorSprite.START;
		};
	}
}