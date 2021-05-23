package com.xharlock.otakusenpai.music.cmds;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.xharlock.otakusenpai.music.core.MusicCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LyricsCmd extends MusicCommand {

	public LyricsCmd(String name) {
		super(name);
		setDescription("Use this command to display the lyrics of the current track. Please keep in mind that the lyrics are in English regardless of the song");
		setUsage(name + " [search terms]");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		LyricsClient client = new LyricsClient();
		Lyrics lyrics = null;
		
		// If no arguments given, look up the current song of the music player
		if (args.length == 0) {
			try {
				lyrics = client.getLyrics("smooth criminal").get();
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			}			
		} else {
			try {
				lyrics = client.getLyrics(String.join(" ", args)).get();
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			}
		}
		
		String content = lyrics.getContent();
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Lyrics | " + lyrics.getTitle() + " | " + lyrics.getAuthor().replace("Lyrics", ""));
		
		Scanner scanner = new Scanner(content);
		String block = "";
		
		while (scanner.hasNextLine()) {
			String s = scanner.nextLine();
			if (s.isBlank()) {
				builder.addField("", block, false);
				block = "";
			} else {
				block += s + "\n";
			}
		}
		builder.addField("", block, false);
		
		sendEmbed(e, builder, true);
		
		scanner.close();
	}

}
