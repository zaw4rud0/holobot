package com.xharlock.otakusenpai.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChatClient {

	public static void doStuff(MessageReceivedEvent e) {

		// #bot-fun
		if (e.getChannel().getIdLong() == 831536494350827591L) {
			e.getJDA().getTextChannelById(755339832917491722L).sendMessage(e.getMessage()).queue();
			return;
		}

		// #bot-fun
		if (e.getChannel().getIdLong() == 831536417665712179L) {
			e.getJDA().getTextChannelById(747776646551175217L).sendMessage(e.getMessage()).queue();
			return;
		}

		// #spam
		if (e.getChannel().getIdLong() == 831536298836754522L) {
			e.getJDA().getTextChannelById(768600365602963496L).sendMessage(e.getMessage()).queue();
			return;
		}

	}

}
