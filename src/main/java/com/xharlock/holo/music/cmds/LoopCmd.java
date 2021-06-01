package com.xharlock.holo.music.cmds;

import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.MusicCommand;
import com.xharlock.holo.music.core.PlayerManager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCmd extends MusicCommand {

	public LoopCmd(String name) {
		super(name);
		setDescription("Use this command to loop the current track");
		setUsage(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		boolean repeating = !musicManager.scheduler.looping;
		musicManager.scheduler.looping = repeating;
		e.getChannel().sendMessageFormat("Loop %s", repeating ? "enabled" : "disabled").queue();
	}
}
