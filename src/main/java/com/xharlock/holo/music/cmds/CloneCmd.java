package com.xharlock.holo.music.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.MusicCommand;
import com.xharlock.holo.music.core.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CloneCmd extends MusicCommand {

	public CloneCmd(String name) {
		super(name);
		setDescription("Use this command to duplicate the currently playing track and add it on top of the queue");
		setUsage(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		AudioTrack current = musicManager.audioPlayer.getPlayingTrack();

		// Check if there are any tracks playing
		if (current == null) {
			builder.setTitle("Error");
			builder.setDescription("I'm not playing any tracks at the moment!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		// Check vc conditions (user and bot in same vc, etc.)
		if (!isUserInSameAudioChannel(e)) {
			builder.setTitle("Not in same voice channel!");
			builder.setDescription("You need to be in the same voice channel as me!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		// Re-order the queue with the cloned track on top of it
		List<AudioTrack> queueList = new ArrayList<>();
		queueList.add(current.makeClone());
		queueList.addAll(musicManager.scheduler.queue);
		musicManager.scheduler.queue.clear();
		for (AudioTrack track : queueList) {
			musicManager.scheduler.queue.offer(track);
		}

		// Get thumbnail
		String uri = musicManager.audioPlayer.getPlayingTrack().getInfo().uri.split("v=")[1].split("&")[0];
		String thumbnail = "https://img.youtube.com/vi/" + uri + "/hqdefault.jpg";

		// Display informations of the cloned track
		builder.setTitle("Cloned track");
		builder.setThumbnail(thumbnail);
		builder.addField("Title", current.getInfo().title, false);
		builder.addField("Uploader", current.getInfo().author, false);
		builder.addField("Link", "[Youtube](" + current.getInfo().uri + ")", false);

		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}