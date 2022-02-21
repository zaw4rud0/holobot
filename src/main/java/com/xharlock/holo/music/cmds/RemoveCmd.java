package com.xharlock.holo.music.cmds;

import com.xharlock.holo.music.core.MusicCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// TODO Command that lets an user remove tracks at a specific position. Helpful to remove rickrolls or other unwanted songs.
public class RemoveCmd extends MusicCommand {

	public RemoveCmd(String name) {
		super(name);
		setDescription("Use this command to remove a track with the given index from the queue");
		setUsage(name + " <index>");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getChannel().sendMessage("This feature is not implemented yet!").queue();
	}
}