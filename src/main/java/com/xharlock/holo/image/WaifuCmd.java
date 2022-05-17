package com.xharlock.holo.image;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.apis.GelbooruAPI;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.database.DBOperations;
import com.xharlock.holo.misc.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Deactivated
@Command(name = "waifu",
		description = "Sends an image of a specified tag from [Gelbooru](https://gelbooru.com/).",
		usage = "<tag>",
		embedColor = EmbedColor.GELBOORU,
		isNSFW = true,
		category = CommandCategory.IMAGE)
public class WaifuCmd extends AbstractCommand {

	private List<String> names;

	public WaifuCmd() {
		// Get waifus from DB
		try {
			names = DBOperations.getWaifuNames();
			Collections.sort(names);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		
		// Owner added a new waifu to the DB
		if (isBotOwner(e) && args.length >= 4 && args[0].equals("add")) {
			String name = args[1];
			String tag = args[2];
			String title = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
			
			builder.setTitle("Waifu added to the database");
			builder.setDescription("Name: " + name + "\n"
					+ "Tag: " + tag + "\n"
					+ "Title: " + title + "\n");
			
			try {
				DBOperations.insertWaifu(name, tag, title);
			} catch (SQLException ex) {
				ex.printStackTrace();
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while adding a new waifu to the database. Please try again in a few minutes.");
			}
			
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true, getEmbedColor());
			names.add(name);
			Collections.sort(names);
			return;
		}
		
		// Display all available tags
		if (args.length == 0 || args[0].toLowerCase(Locale.UK).equals("list")) {
			builder.setTitle("Image Tags");
			builder.setDescription(getCategoriesString());
			builder.addField("Usage", "`" + getPrefix(e) + "image <tag>`", false);
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true, getEmbedColor());
			return;
		}
		
		// Tag not found
		if (!names.contains(args[0].toLowerCase(Locale.UK))) {
			builder.setTitle("Tag not found");
			builder.setDescription("Use `" + getPrefix(e) + "image` to see all available tags");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true, getEmbedColor());
			return;
		}
	
		String name = args[0].toLowerCase(Locale.UK);
		
		try {
			ResultSet rs = DBOperations.getWaifu(name);
			rs.next();
			String tag = rs.getString("Tag");
			String url;
			
			// Keeps fetching a new url until the url isn't on the blocklist
			do {
				url = getImage(tag);
			} while (BlockCmd.blocked.contains(url) || BlockCmd.blockRequests.contains(url));
			
			builder.setTitle(rs.getString("Title"));
			builder.setImage(url);

			rs.close();
		} catch (SQLException | IOException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an image. Please try again in a few minutes!");
		}
		
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true, getEmbedColor());
	}
	
	/**
	 * Get the right image from Gelbooru
	 */
	@Nullable
	private String getImage(String tag) throws IOException {
		JsonArray array = GelbooruAPI.getJsonArray(GelbooruAPI.Rating.SAFE, GelbooruAPI.Sort.RANDOM, 1, tag);
		if (array == null || array.size() == 0) {
			return null;
		}
		JsonObject obj = array.get(0).getAsJsonObject();
		return obj.has("large_file_url") ? obj.get("large_file_url").getAsString() : obj.get("file_url").getAsString();
	}

	/**
	 * Properly display all available tags as a single String
	 */
	private String getCategoriesString() {
		return names.toString().replace("]", "`").replace("[", "`").replace(",", "`").replace(" ", ", `");
	}
}