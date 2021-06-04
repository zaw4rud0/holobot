package com.xharlock.holo.music.cmds;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.xharlock.holo.music.core.MusicCommand;

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
		e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		
		LyricsClient client = new LyricsClient();
		Lyrics lyrics = null;
		
		if (args.length == 0) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please provide a song name");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		} else {
			try {
				lyrics = client.getLyrics(String.join(" ", args)).get();
			} catch (InterruptedException | ExecutionException ex) {
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while communicating with the API. Please try again in a few minutes!");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}
		}
		
		if (lyrics == null || lyrics.getContent() == null || lyrics.getContent().equals("")) {
			builder.setTitle("Error");
			builder.setDescription("Couldn't find any lyrics for `" + String.join(" ", args) + "`");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		String text = lyrics.getContent();		
		builder.setTitle("Lyrics | " + lyrics.getTitle() + " by " + lyrics.getAuthor().replace("Lyrics", ""));
		
		Scanner scanner = new Scanner(text);
		String block = "";
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isBlank()) {
				builder.addField("", block, false);
				block = "";
			} else {
				// A field can't contain more than 1024 characters
				if (block.length() + line.length() > 1024) {
					builder.addField("", block, false);
					builder.addField("", line, false);
					block = "";
				} else
					block += line + "\n";				
			}
		}
		
		builder.addField("", block, false);		
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);		
		scanner.close();
	}
}
