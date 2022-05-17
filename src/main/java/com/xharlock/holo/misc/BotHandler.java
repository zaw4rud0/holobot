package com.xharlock.holo.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Locale;
import java.util.Random;

/**
 * Class that handles everything related to other fellow bots in a guild
 */
public class BotHandler extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Ignore every message from non-bots and ignore self
		if (e.isWebhookMessage() || !e.getAuthor().isBot() || e.getAuthor().equals(e.getJDA().getSelfUser())) {
			return;
		}

		// General Kenobi meme
		String[] replies = {"General Kenobi", "Genewal Kenowi UwU", "General Kenobi-chan", "OwO"};
		
		if (e.getMessage().getContentRaw().toLowerCase(Locale.UK).contains("hello there")) {
			e.getMessage().reply(replies[new Random().nextInt(replies.length)]).queue();
		}
	}
}