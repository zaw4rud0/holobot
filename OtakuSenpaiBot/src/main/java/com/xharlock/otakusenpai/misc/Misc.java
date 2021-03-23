package com.xharlock.otakusenpai.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Misc extends ListenerAdapter {

	public void onMessageReceived(MessageReceivedEvent e) {
		heart(e);
	}

	private void heart(MessageReceivedEvent e) {
		if (e.getMessage().getContentRaw().contains("<3")) {
			e.getMessage().addReaction(Emojis.HEART.getAsReaction()).queue();
		}
	}
}
