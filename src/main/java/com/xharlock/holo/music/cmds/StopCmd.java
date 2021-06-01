package com.xharlock.holo.music.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.MusicCommand;
import com.xharlock.holo.music.core.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopCmd extends MusicCommand {

	public StopCmd(String name) {
		super(name);
		setDescription("Use this command to stop my current track and to clear the queue");
		setUsage(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		if (musicManager.scheduler.audioPlayer.getPlayingTrack() == null && musicManager.scheduler.queue.size() == 0) {
			builder.setTitle("Error");
			builder.setDescription("I'm currently idle!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		musicManager.scheduler.audioPlayer.stopTrack();
		musicManager.scheduler.queue.clear();
		
		builder.setTitle("Success");
		builder.setDescription("Stopped current track and cleared queue!");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}

}
