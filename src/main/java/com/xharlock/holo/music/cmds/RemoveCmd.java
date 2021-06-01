package com.xharlock.holo.music.cmds;

import com.xharlock.holo.music.core.MusicCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveCmd extends MusicCommand {

	public RemoveCmd(String name) {
		super(name);
		setDescription("Use this command to remove a track with the given index from the queue");
		setUsage(name + " <index>");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		// Check if index is out of bounds
		
		
		// Get track from the queue
		
		
	}

}
