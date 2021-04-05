package com.xharlock.otakusenpai.music.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayerManager {

	private static PlayerManager INSTANCE;
	private Map<Long, GuildMusicManager> musicManagers;
	private AudioPlayerManager audioPlayerManager;

	public PlayerManager() {
		this.musicManagers = new HashMap<Long, GuildMusicManager>();
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager = new DefaultAudioPlayerManager());
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
			GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.getAudioPlayerHandler());
			return guildMusicManager;
		});
	}

	public void loadAndPlay(MessageReceivedEvent e, String trackURL) {
		final GuildMusicManager musicManager = this.getMusicManager(e.getGuild());
		this.audioPlayerManager.loadItemOrdered((Object) musicManager, trackURL,
				new AudioLoadResultHandler() {
					public void trackLoaded(final AudioTrack track) {
						musicManager.scheduler.enqueue(track);
						e.getChannel().sendMessage((CharSequence) "Added to the queue: `")
								.append((CharSequence) track.getInfo().title).append((CharSequence) "` by `")
								.append((CharSequence) track.getInfo().author).append((CharSequence) "`").queue();
					}

					public void playlistLoaded(AudioPlaylist playlist) {
						final List<AudioTrack> tracks = (List<AudioTrack>) playlist.getTracks();
						e.getChannel().sendMessage((CharSequence) "Added to the queue: `")
								.append((CharSequence) String.valueOf(tracks.size()))
								.append((CharSequence) "` tracks from playlist `")
								.append((CharSequence) playlist.getName()).append((CharSequence) "`").queue();
						for (AudioTrack track : tracks) {
							musicManager.scheduler.enqueue(track);
						}
					}

					public void noMatches() {
						e.getChannel().sendMessage((CharSequence) "Something went wrong").queue();
					}

					public void loadFailed(FriendlyException exception) {
						e.getChannel().sendMessage((CharSequence) "Something went wrong").queue();
						exception.printStackTrace();
					}
				});
	}

	public static PlayerManager getInstance() {
		if (PlayerManager.INSTANCE == null)
			PlayerManager.INSTANCE = new PlayerManager();
		return PlayerManager.INSTANCE;
	}
}
