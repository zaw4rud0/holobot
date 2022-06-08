package dev.zawarudo.holo.music.cmds;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.core.AbstractMusicCommand;
import dev.zawarudo.holo.music.core.GuildMusicManager;
import dev.zawarudo.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "clone",
		description = "Duplicates the currently playing track and adds it on top of the queue.",
		category = CommandCategory.MUSIC)
public class CloneCmd extends AbstractMusicCommand {

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

		// Display information of the cloned track
		builder.setTitle("Cloned track");
		builder.setThumbnail(thumbnail);
		builder.addField("Title", current.getInfo().title, false);
		builder.addField("Uploader", current.getInfo().author, false);
		builder.addField("Link", "[Youtube](" + current.getInfo().uri + ")", false);

		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}