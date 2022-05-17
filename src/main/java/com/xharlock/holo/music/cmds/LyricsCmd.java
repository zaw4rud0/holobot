package com.xharlock.holo.music.cmds;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Command(name = "lyrics",
		description = "Displays the lyrics of a given song.",
		usage = "<search terms>",
		category = CommandCategory.MUSIC)
public class LyricsCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		sendTyping(e);

		EmbedBuilder builder = new EmbedBuilder();

		LyricsClient client = new LyricsClient();
		Lyrics lyrics;

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
				} else {
					block += line + "\n";
				}
			}
		}

		builder.addField("", block, false);
		sendEmbed(e, builder, true);
		scanner.close();
	}
}