package com.xharlock.holo.anime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.misc.Emojis;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReadCmd extends Command {

	public ReadCmd(String name) {
		super(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle("Manga Reader");
		builder.setDescription("Fetching data from the API...");
		Message msg = sendEmbedAndGetMessage(e, builder, true);
		
		String title = String.join(" ", args);
		List<String> pages = new ArrayList<>();
		
		try {
			String manga_id = MangaDexAPI.search(title).get(0).getAsJsonObject().getAsJsonObject("data").get("id").getAsString();
			boolean dataSaver = false;
			String chapter_id = MangaDexAPI.getChapterId(manga_id, "1");
			pages = MangaDexAPI.getPages(chapter_id, dataSaver);
		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while communicating with the API. Please try again in a few minutes!");
			msg.editMessage(builder.build()).queue();
			return;
		}
		
		msg.addReaction(Emojis.ARROW_LEFT.getAsReaction()).queue();
		msg.addReaction(Emojis.ARROW_RIGHT.getAsReaction()).queue();
		
		// Set to manga title
		builder.setTitle("Manga Reader");
		
		// Show page number
		builder.setDescription("test");
		
		// Current page
		builder.setImage(pages.get(0));
		
		msg.editMessage(builder.build()).queue();
		
	}

}
