package com.xharlock.otakusenpai.music.cmds;

import com.xharlock.otakusenpai.music.core.MusicCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CloneCmd extends MusicCommand {

	public CloneCmd(String name) {
		super(name);
		setDescription("Use this command to duplicate the currently playing track and add it on top of the queue");
		setUsage(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		
		// Check if there are any tracks playing
		
		
		// Re-order the queue with the cloned track on top of it
		
		
	}

}
