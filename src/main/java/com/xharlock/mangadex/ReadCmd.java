package com.xharlock.mangadex;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		boolean dataSaver = false;
		
		String manga_id = "";
		String chapter = "1";
		
		try {
			manga_id = MangaDexAPI.search(title).get(0).getAsJsonObject().getAsJsonObject("data").get("id").getAsString();			
		} catch (IOException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while communicating with the API. Please try again in a few minutes!");
			msg.editMessage(builder.build()).queue();
			return;
		}
		
		List<String> pageUrls = new ArrayList<>();
		
		try {
			MangaDexDatabase.connect();
			ResultSet chapterSet = MangaDexDatabase.query("SELECT * FROM Chapters WHERE MangaId = \'" + manga_id + "\' AND Chapter = \'" + chapter + "\';");
			if (!chapterSet.next()) {
				System.out.println("No chapter found!");
				return;
			}
			String chapterId = chapterSet.getString("ChapterId");
			String chapterHash = chapterSet.getString("Hash");
			ResultSet pageSet = MangaDexDatabase.query("SELECT * FROM Pages WHERE ChapterId = \'" + chapterId + "\' AND DataSaver = \'" + dataSaver + "\';");
			String base_url = "https://uploads.mangadex.org/" + (dataSaver ? "data-saver" : "data") + "/" + chapterHash + "/";
			while (pageSet.next()) {
				pageUrls.add(base_url + pageSet.getString("FileName"));
			}
			MangaDexDatabase.disconnect();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		msg.addReaction(Emojis.ARROW_LEFT.getAsBrowser()).queue();
		msg.addReaction(Emojis.ARROW_RIGHT.getAsBrowser()).queue();
		
		// Set to manga title
		builder.setTitle("Manga Reader");
		
		// Show page number
		builder.setDescription("test");
		
		// Current page
		builder.setImage(pageUrls.get(0));
		
		msg.editMessage(builder.build()).queue();
		
		for (String s : pageUrls) {
			e.getChannel().sendMessage(s).queue();
		}
	}

}
