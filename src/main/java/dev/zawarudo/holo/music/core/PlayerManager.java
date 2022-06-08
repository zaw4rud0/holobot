package dev.zawarudo.holo.music.core;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

	private static PlayerManager INSTANCE;
	private final Map<Long, GuildMusicManager> musicManagers;
	private final AudioPlayerManager audioPlayerManager;

	public PlayerManager() {
		musicManagers = new HashMap<>();
		AudioSourceManagers.registerRemoteSources(audioPlayerManager = new DefaultAudioPlayerManager());
		AudioSourceManagers.registerLocalSource(audioPlayerManager);
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
			GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.getAudioPlayerHandler());
			return guildMusicManager;
		});
	}

	public void loadAndPlay(MessageReceivedEvent e, EmbedBuilder builder, String trackUrl) {
		GuildMusicManager musicManager = getMusicManager(e.getGuild());
		audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				musicManager.scheduler.enqueue(track);

				String uri = track.getInfo().uri.split("v=")[1].split("&")[0];
				String thumbnail = "https://img.youtube.com/vi/" + uri + "/hqdefault.jpg";

				builder.setTitle("Added to the queue");
				builder.setThumbnail(thumbnail);
				builder.addField("Title", track.getInfo().title, false);
				builder.addField("Uploader", track.getInfo().author, false);
				builder.addField("Link", "[Youtube](" + trackUrl + ")", false);

				e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.MINUTES));
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				List<AudioTrack> tracks = playlist.getTracks();
				for (AudioTrack track : tracks) {
					musicManager.scheduler.enqueue(track);
				}
				builder.setTitle("Added to the queue");
				builder.setDescription("`" + tracks.size() + "` tracks from playlist `" + playlist.getName() + "`");
				builder.addField("Link", "[Youtube](" + trackUrl + ")", false);

				e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.MINUTES));
			}

			@Override
			public void noMatches() {
				builder.setTitle("No matches!");
				builder.setDescription("Something went wrong! Please contact my owner and provide this track url: " + trackUrl);
				e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.MINUTES));
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				builder.setTitle("Load failed!");
				builder.setDescription("Something went wrong! Please contact my owner and provide this track url: " + trackUrl);
				e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.MINUTES));
			}
		});
	}

	public static PlayerManager getInstance() {
		if (PlayerManager.INSTANCE == null) {
			PlayerManager.INSTANCE = new PlayerManager();
		}
		return PlayerManager.INSTANCE;
	}
}