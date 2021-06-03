package com.xharlock.holo.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Misc extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		String content = e.getMessage().getContentRaw();

		// Add ❤️
		if (content.contains(":heart:") || content.contains("<3") || content.contains("❤️")) {
			e.getMessage().addReaction(Emojis.HEART.getAsBrowser()).queue();
		}
	}
}
