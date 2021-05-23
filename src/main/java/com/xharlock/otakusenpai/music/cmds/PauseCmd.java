package com.xharlock.otakusenpai.music.cmds;

import com.xharlock.otakusenpai.music.core.MusicCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCmd extends MusicCommand {

	public PauseCmd(String name) {
		super(name);
		setDescription("Use this command to pause the music player");
		setUsage(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		
		
	}

}
