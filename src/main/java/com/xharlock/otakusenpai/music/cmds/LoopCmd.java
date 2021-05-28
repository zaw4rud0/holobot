package com.xharlock.otakusenpai.music.cmds;

import com.xharlock.otakusenpai.music.core.GuildMusicManager;
import com.xharlock.otakusenpai.music.core.MusicCommand;
import com.xharlock.otakusenpai.music.core.PlayerManager;

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
		
		e.getChannel().sendTyping().queue();
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		boolean repeating = !musicManager.scheduler.looping;
		musicManager.scheduler.looping = repeating;
		e.getChannel().sendMessageFormat("Loop %s", repeating ? "enabled" : "disabled").queue();
	}
}
