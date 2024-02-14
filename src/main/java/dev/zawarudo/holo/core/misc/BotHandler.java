package dev.zawarudo.holo.core.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Random;

/**
 * Class that handles everything related to other fellow bots within a guild.
 */
public class BotHandler extends ListenerAdapter {

	private static final Random RANDOM = new Random();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent e) {
		// Ignore every message from non-bots and ignore self
		if (e.isWebhookMessage() || !e.getAuthor().isBot() || e.getAuthor().equals(e.getJDA().getSelfUser())) {
			return;
		}

		// General Kenobi meme
		String[] replies = {"General Kenobi", "Genewal Kenowi UwU", "General Kenobi-chan", "OwO"};
		String msg = replies[RANDOM.nextInt(replies.length)];
		if (e.getMessage().getContentRaw().toLowerCase(Locale.UK).contains("hello there")) {
			e.getMessage().reply(msg).queue();
		}
	}
}