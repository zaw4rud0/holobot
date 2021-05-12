package com.xharlock.otakusenpai.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.core.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AnimeSearchCmd extends Command {

	private EventWaiter waiter;
	
	public AnimeSearchCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to search for an anime");
		setUsage(name + " <anime name>");
		setExample(name + " one piece");
		this.waiter = waiter;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
	}

}
