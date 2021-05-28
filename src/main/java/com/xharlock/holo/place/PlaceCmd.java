package com.xharlock.holo.place;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlaceCmd extends PlaceCommand {

	public PlaceCmd(String name) {
		super(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		
		
	}

}
