package com.xharlock.holo.music.cmds;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "stop",
		description = "Stops the current song and clears the queue.",
		ownerOnly = true,
		category = CommandCategory.MUSIC)
public class StopCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		if (musicManager.scheduler.audioPlayer.getPlayingTrack() == null && musicManager.scheduler.queue.isEmpty()) {
			builder.setTitle("Error");
			builder.setDescription("I'm currently idle!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		musicManager.clear();
		
		builder.setTitle("Success");
		builder.setDescription("Stopped current track and cleared queue!");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}
}