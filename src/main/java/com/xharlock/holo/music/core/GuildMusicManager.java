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
		this.audioPlayer = manager.createPlayer();
		this.scheduler = new TrackScheduler(this.audioPlayer);
		this.audioPlayer.addListener((AudioEventListener) this.scheduler);
		this.audioPlayerHandler = new AudioPlayerSendHandler(this.audioPlayer);		
		this.voting = false;
		this.counter = new AtomicInteger(0);
	}

	public AudioPlayerSendHandler getAudioPlayerHandler() {
		return this.audioPlayerHandler;
	}

	public void setVoting(boolean isVoting) {
		this.voting = isVoting;
	}

	public boolean isVoting() {
		return voting;
	}

	public AtomicInteger getCounter() {
		return this.counter;
	}
}
