package com.xharlock.holo.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Misc extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		String content = e.getMessage().getContentRaw();

		// Add :heart:
		if (content.contains(":heart:") || content.contains("<3") || content.contains("т?дя╕?")) {
			e.getMessage().addReaction(Emojis.HEART.getAsBrowser()).queue();
		}
		
		if (e.getChannel().getIdLong() == 831536298836754522L) {
			e.getJDA().getTextChannelById(768600365602963496L).sendMessage(e.getMessage()).queue();
		}
	}
}
