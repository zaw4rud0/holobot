package dev.zawarudo.holo.music.cmds;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.Emote;
import dev.zawarudo.holo.music.AbstractMusicCommand;
import dev.zawarudo.holo.music.GuildMusicManager;
import dev.zawarudo.holo.music.PlayerManager;
import dev.zawarudo.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Command(name = "now",
		description = "Shows information about the current track.",
		alias = {"np", "nowplaying"},
		category = CommandCategory.MUSIC)
public class NowPlayingCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		AudioPlayer audioPlayer = musicManager.audioPlayer;
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(Emote.SPEAKER_LOUD.getAsText() + " Track Information");
		
		if (audioPlayer.getPlayingTrack() == null) {
			builder.setDescription("I'm not playing any tracks right now");
			sendEmbed(e, builder, true, 15, TimeUnit.SECONDS);
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
		
		sendEmbed(e, builder, true, 1, TimeUnit.MINUTES);
	}
}