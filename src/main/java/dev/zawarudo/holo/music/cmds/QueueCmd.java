package dev.zawarudo.holo.music.cmds;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.core.AbstractMusicCommand;
import dev.zawarudo.holo.music.core.GuildMusicManager;
import dev.zawarudo.holo.music.core.PlayerManager;
import dev.zawarudo.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Command(name = "queue",
		description = "Shows the current queue. You can also use `history` as an additional argument to view the last 10 tracks that was played.",
		usage = "[history]",
		alias = {"q"},
		category = CommandCategory.MUSIC)
public class QueueCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		// Show queue history
		if (args.length >= 1 && args[0].equals("history")) {
			displayHistory(e);
		}

		// Show queue
		else {
			displayQueue(e);
		}
	}

	/**
	 * Method to display current queue
	 */
	private void displayQueue(MessageReceivedEvent e) {
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Queue");

		if (queue.isEmpty()) {
			builder.setDescription("My queue is empty!");
			sendEmbed(e, builder, 15L, TimeUnit.SECONDS, true);
			return;
		}

		int trackCount = Math.min(queue.size(), 12);
		List<AudioTrack> trackList = new ArrayList<>(queue);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < trackCount; ++i) {
			AudioTrack track = trackList.get(i);
			AudioTrackInfo info = track.getInfo();
			sb.append(String.format("`#%02d %s by %s [%s]`", i + 1, info.title, info.author,
					Formatter.formatTrackTime(track.getDuration()))).append("\n");
		}

		if (trackList.size() > trackCount) {
			sb.append("And `").append(trackList.size() - trackCount).append("` more...");
		}

		// Get total duration of the queue + current track
		AudioTrack current = musicManager.audioPlayer.getPlayingTrack();
		long duration = current != null ? current.getDuration() - current.getPosition() : 0L;
		for (AudioTrack track : trackList) {
			duration += track.getDuration();
		}

		builder.setDescription(sb.toString());
		builder.addField("Total Duration", Formatter.formatTrackTime(duration), false);

		sendEmbed(e, builder, 1L, TimeUnit.MINUTES, true);
	}

	/**
	 * Method to display the last 10 tracks
	 */
	private void displayHistory(MessageReceivedEvent e) {
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		BlockingQueue<AudioTrack> history = musicManager.scheduler.history;

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Queue History");

		if (history == null || history.isEmpty()) {
			builder.setDescription("I didn't play any tracks recently!");
			sendEmbed(e, builder, 15L, TimeUnit.SECONDS, true);
			return;
		}

		int trackCount = Math.min(history.size(), 10);
		List<AudioTrack> historyList = new ArrayList<>(history);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < trackCount; ++i) {
			AudioTrack track = historyList.get(i);
			AudioTrackInfo info = track.getInfo();
			sb.append(String.format("`#%02d %s [%s]` [%s]", i + 1, info.title,
					Formatter.formatTrackTime(track.getDuration()), "[link](" + info.uri + ")")).append("\n");
		}

		builder.setDescription(sb.toString());
		sendEmbed(e, builder, 1L, TimeUnit.MINUTES, true);
	}
}