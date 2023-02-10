package dev.zawarudo.holo.music.cmds;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.AbstractMusicCommand;
import dev.zawarudo.holo.music.GuildMusicManager;
import dev.zawarudo.holo.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Command(name = "clone",
		description = "Duplicates the currently playing track and adds it on top of the queue.",
		category = CommandCategory.MUSIC)
public class CloneCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		EmbedBuilder builder = new EmbedBuilder();
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

		AudioTrack current = musicManager.audioPlayer.getPlayingTrack();

		// Check if there are any tracks playing
		if (current == null) {
			sendErrorEmbed(event, "I'm not playing any tracks at the moment!");
			return;
		}

		// Check vc conditions (user and bot in same vc, etc.)
		if (!isUserInSameAudioChannel(event)) {
			sendErrorEmbed(event, "You need to be in the same voice channel as me to use this command!");
			return;
		}

		musicManager.scheduler.enqueueFirst(current.makeClone());

		// Get thumbnail
		String uri = musicManager.audioPlayer.getPlayingTrack().getInfo().uri.split("v=")[1].split("&")[0];
		String thumbnail = "https://img.youtube.com/vi/" + uri + "/hqdefault.jpg";

		// Display information of the cloned track
		builder.setTitle("Cloned track");
		builder.setThumbnail(thumbnail);
		builder.addField("Title", current.getInfo().title, false);
		builder.addField("Uploader", current.getInfo().author, false);
		builder.addField("Link", "[Youtube](" + current.getInfo().uri + ")", false);

		sendEmbed(event, builder, true, 1, TimeUnit.MINUTES);
	}
}