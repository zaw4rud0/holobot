package com.xharlock.otakusenpai.music.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {

	public BlockingQueue<AudioTrack> queue;
	public final AudioPlayer audioPlayer;
	public boolean looping;
	public boolean paused;

	public TrackScheduler(AudioPlayer player) {
		this.queue = new LinkedBlockingQueue<>();
		this.audioPlayer = player;
		this.looping = false;
		this.paused = false;
	}

	public void enqueue(AudioTrack track) {
		if (!this.audioPlayer.startTrack(track, true))
			this.queue.offer(track);
	}

	public void playNext() {
		this.audioPlayer.startTrack(this.queue.poll(), false);
	}

	public void shuffle() {
		List<AudioTrack> queueList = new ArrayList<>(this.queue);
		Collections.shuffle(queueList);
		this.queue.clear();
		for (AudioTrack track : queueList)
			this.queue.offer(track);
	}

	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			if (this.looping)
				this.audioPlayer.startTrack(track.makeClone(), false);
			else
				this.playNext();
		}
	}
}
