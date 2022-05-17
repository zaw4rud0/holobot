package com.xharlock.holo.music.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

	/** The queue of tracks */
	public BlockingQueue<AudioTrack> queue;
	/** The last 10 {@link AudioTrack}s from the {@link AudioPlayer} */
	public BlockingQueue<AudioTrack> history;
	public final AudioPlayer audioPlayer;

	// TODO Actually implement looping and paused
	public boolean looping;
	public boolean paused;

	public TrackScheduler(AudioPlayer player) {
		queue = new LinkedBlockingQueue<>();
		history = new LinkedBlockingQueue<>();
		audioPlayer = player;
		looping = false;
		paused = false;
	}

	/**
	 * Method to add a given {@link AudioTrack} to the queue. If the queue is empty,
	 * it will be played by the {@link AudioPlayer}.
	 */
	public void enqueue(AudioTrack track) {
		if (queue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
			addToHistory(track);
		}
		if (!audioPlayer.startTrack(track, true)) {
			queue.offer(track);
		}
	}

	/**
	 * Method to shuffle the queue, i.e. the order of {@link AudioTrack}s will be
	 * randomized.
	 */
	public void shuffle() {
		List<AudioTrack> queueList = new ArrayList<>(queue);
		queue.clear();
		Collections.shuffle(queueList);
		for (AudioTrack track : queueList) {
			queue.offer(track);
		}
	}

	/**
	 * Method to add a given {@AudioTrack} to the history. Note that the history can
	 * only contain 10 items, meaning the oldest ones will be removed.
	 */
	public void addToHistory(AudioTrack track) {
		List<AudioTrack> historyList = new ArrayList<>(history);
		history.clear();
		historyList.add(0, track);
		while (historyList.size() > 10) {
			historyList.remove(10);
		}
		for (AudioTrack t : historyList) {
			history.offer(t);
		}
	}

	/**
	 * Method which decides what happens after the current {@link AudioTrack}
	 * finishes.
	 */
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			if (looping) {
				audioPlayer.startTrack(track.makeClone(), false);
			} else {
				playNext();
			}
		}
	}

	/**
	 * Method to play the next {@link AudioTrack} in the queue. If the queue is empty, the
	 * {@link AudioPlayer} will simply stop.
	 */
	public void playNext() {
		AudioTrack track = queue.poll();
		audioPlayer.startTrack(track, false);
		if (track != null) {
			addToHistory(track);
		}
	}
}
