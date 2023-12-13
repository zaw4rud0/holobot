package dev.zawarudo.holo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import java.util.concurrent.atomic.AtomicInteger;

public class GuildMusicManager {

	public final AudioPlayer audioPlayer;
	public final TrackScheduler scheduler;
	private final AudioPlayerSendHandler audioPlayerHandler;
	private boolean voting;
	private final AtomicInteger counter;

	public GuildMusicManager(AudioPlayerManager manager) {
		audioPlayer = manager.createPlayer();
		scheduler = new TrackScheduler(audioPlayer);
		audioPlayer.addListener(scheduler);
		audioPlayerHandler = new AudioPlayerSendHandler(audioPlayer);		
		voting = false;
		counter = new AtomicInteger(0);
	}

	/**
	 * Returns the instance of {@link AudioPlayerSendHandler}.
	 */
	public AudioPlayerSendHandler getAudioPlayerHandler() {
		return audioPlayerHandler;
	}

	/**
	 * Resets the voting.
	 */
	public void resetVoting() {
		voting = false;
		counter.set(0);
	}

	/**
	 * Sets the voting variable that keeps track of whether there is a voting ongoing.
	 *
	 * @param isVoting Whether the guild is voting or not.
	 */
	public void setVoting(boolean isVoting) {
		voting = isVoting;
	}

	/**
	 * Checks if there is a voting session going on.
	 */
	public boolean isVoting() {
		return voting;
	}

	/**
	 * Returns a counter that is used to determine if a vote has passed.
	 */
	public AtomicInteger getVoteCounter() {
		return counter;
	}
	
	/**
	 * Clears the GuildMusicManager.
	 */
	public void clear() {
		scheduler.looping = false;
		scheduler.queue.clear();
		audioPlayer.stopTrack();
	}
}