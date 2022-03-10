package com.xharlock.holo.music.core;

import java.util.concurrent.atomic.AtomicInteger;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;

public class GuildMusicManager {

	public AudioPlayer audioPlayer;
	public TrackScheduler scheduler;
	private AudioPlayerSendHandler audioPlayerHandler;
	private boolean voting;
	private AtomicInteger counter;

	public GuildMusicManager(AudioPlayerManager manager) {
		audioPlayer = manager.createPlayer();
		scheduler = new TrackScheduler(audioPlayer);
		audioPlayer.addListener((AudioEventListener) scheduler);
		audioPlayerHandler = new AudioPlayerSendHandler(audioPlayer);		
		voting = false;
		counter = new AtomicInteger(0);
	}

	public AudioPlayerSendHandler getAudioPlayerHandler() {
		return audioPlayerHandler;
	}

	public void setVoting(boolean isVoting) {
		voting = isVoting;
	}

	public boolean isVoting() {
		return voting;
	}

	public AtomicInteger getCounter() {
		return counter;
	}
}