package com.xharlock.holo.music.cmds;

import com.xharlock.holo.music.core.MusicCommand;

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
		e.getChannel().sendMessage("This feature is not implemented yet!").queue();
	}
}
