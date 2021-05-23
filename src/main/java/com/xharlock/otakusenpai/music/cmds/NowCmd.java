package com.xharlock.otakusenpai.music.cmds;

import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.xharlock.otakusenpai.misc.Emojis;
import com.xharlock.otakusenpai.music.core.*;
import com.xharlock.otakusenpai.utils.Formatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowCmd extends MusicCommand {

	public NowCmd(String name) {
		super(name);
		setDescription("Use this command to display informations about the current track");
		setUsage(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		AudioPlayer audioPlayer = musicManager.audioPlayer;
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(Emojis.SPEAKER.getAsText() + " Track Information");
		
		if (audioPlayer.getPlayingTrack() == null) {
			builder.setDescription("I'm not playing any tracks right now");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
		
		String timestamp = "[`" + Formatter.formatTrackTime(audioPlayer.getPlayingTrack().getPosition()) + "`|`"
				+ Formatter.formatTrackTime(audioPlayer.getPlayingTrack().getDuration()) + "`]";
		
		// Get youtube thumbnail
		String uri = info.uri.split("v=")[1].split("&")[0];
		String thumbnail = "https://img.youtube.com/vi/" + uri + "/hqdefault.jpg";
		
		builder.setThumbnail(thumbnail);
		builder.addField("Title", info.title, false);
		builder.addField("Current Timestamp", timestamp, true);
		builder.addField("Link", "[Youtube](" + info.uri + ")", false);
		
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}

}
