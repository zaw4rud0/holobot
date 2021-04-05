package com.xharlock.otakusenpai.music.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;

public class GuildMusicManager {

	public AudioPlayer audioPlayer;
	public TrackScheduler scheduler;
	private AudioPlayerHandler audioPlayerHandler;
	
	public GuildMusicManager(AudioPlayerManager manager) {
		this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener((AudioEventListener)this.scheduler);
        this.audioPlayerHandler = new AudioPlayerHandler(this.audioPlayer);
	}
	
	public AudioPlayerHandler getAudioPlayerHandler() {
        return this.audioPlayerHandler;
    }
}
