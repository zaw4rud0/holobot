package com.xharlock.holo.music.cmds;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.Emoji;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.PlayerManager;
import com.xharlock.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "now",
		description = "Shows information about the current track.",
		alias = {"np", "nowplaying"},
		category = CommandCategory.MUSIC)
public class NowPlayingCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		AudioPlayer audioPlayer = musicManager.audioPlayer;
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(Emoji.SPEAKER_LOUD.getAsText() + " Track Information");
		
		if (audioPlayer.getPlayingTrack() == null) {
			builder.setDescription("I'm not playing any tracks right now");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
		
		String timestamp = "[`" + Formatter.formatTrackTime(audioPlayer.getPlayingTrack().getPosition()) + "`|`"
				+ Formatter.formatTrackTime(audioPlayer.getPlayingTrack().getDuration()) + "`]";
		
		// Get YouTube thumbnail
		String uri = info.uri.split("v=")[1].split("&")[0];
		String thumbnail = "https://img.youtube.com/vi/" + uri + "/hqdefault.jpg";
		
		builder.setThumbnail(thumbnail);
		builder.addField("Title", info.title, false);
		builder.addField("Current Timestamp", timestamp, true);
		builder.addField("Link", "[Youtube](" + info.uri + ")", false);
		
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}