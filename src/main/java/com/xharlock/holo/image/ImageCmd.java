package com.xharlock.holo.image;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.database.Database;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ImageCmd extends Command {

	private List<String> names;

	public ImageCmd(String name) {
		super(name);
		setDescription("Use this command to get an image of a given tag.");
		setAliases(List.of("img"));
		setUsage(name + " [tag]");
		setIsNSFW(true);
		setCommandCategory(CommandCategory.IMAGE);

		// Get waifus from DB
		try {
			names = getWaifuNames();
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
				insertWaifu(name, tag, title);
			} catch (SQLException ex) {
				ex.printStackTrace();
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while adding a new waifu to the database. Please try again in a few minutes.");
			}
			
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
			names.add(name);
			Collections.sort(names);
			return;
		}
		
		// Display all available tags
		if (args.length == 0 || args[0].toLowerCase(Locale.UK).equals("list")) {
			builder.setTitle("Image Tags");
			builder.setDescription(getCategoriesString());
			builder.addField("Usage", "`" + getPrefix(e) + "image <tag>`", false);
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
			return;
		}
		
		// Tag not found
		if (!names.contains(args[0].toLowerCase(Locale.UK))) {
			builder.setTitle("Tag not found");
			builder.setDescription("Use `" + getPrefix(e) + "image` to see all available tags");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
	
		String name = args[0].toLowerCase(Locale.UK);
		
		try {
			ResultSet rs = getWaifu(name);
			rs.next();
			String tag = rs.getString("Tag");
			String url = null;
			
			// Keeps fetching a new url until the url isn't on the blocklist
			do {
				url = getImage(tag);
			} while (BlockCmd.blocked.contains(url) || BlockCmd.blockRequests.contains(url));
			
			builder.setTitle(rs.getString("Title"));
			builder.setImage(url);
			
			Database.disconnect();
		} catch (SQLException | IOException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an image. Please try again in a few minutes!");
		}
		
		sendEmbed(e, builder, true);
	}	
	
	/**
	 * Get all the waifu names from DB
	 */
	public static List<String> getWaifuNames() throws SQLException {
		String s = "SELECT Id FROM Gelbooru";
		Database.connect();
		ResultSet rs = Database.query(s);
		List<String> names = new ArrayList<>();
		while (rs.next()) {
			names.add(rs.getString("Id"));
		}
		Database.disconnect();
		return names;
	}
	
	/**
	 * Add a new waifu to the image command
	 * 
	 * @param name = Name of waifu
	 * @param tag = Gelbooru tag of waifu
	 * @param title = Title of the embed
	 */
	public static boolean insertWaifu(String name, String tag, String title) throws SQLException {
		title = title.replace("'", "''");
		Database.connect();
		String s = "INSERT into Gelbooru (Id, Tag, Title) VALUES (\'" + name + "\', \'" + tag + "\', \'" + title + "\');";
		boolean success = Database.execute(s);
		Database.disconnect();
		return success;
	}
	
	/**
	 * Get the Gelbooru tag and the embed title using the name of the waifu
	 */
	public static ResultSet getWaifu(String name) throws SQLException {
		String s = "SELECT * FROM Gelbooru WHERE Id = \'" + name + "\';";
		Database.connect();
		ResultSet rs = Database.query(s);
		return rs;
	}
	
	/**
	 * Method to get the right image from Gelbooru
	 */
	private String getImage(String tag) throws IOException {		
		JsonObject object = GelbooruAPI.getJsonArray(GelbooruAPI.Rating.SAFE, GelbooruAPI.Sort.RANDOM, 1, tag).get(0).getAsJsonObject();		
		return object.has("large_file_url") ? object.get("large_file_url").getAsString() : object.get("file_url").getAsString();
	}

	/**
	 * Method to properly display all available tags as a single String
	 */
	private String getCategoriesString() {
		return names.toString().replace("]", "`").replace("[", "`").replace(",", "`").replace(" ", ", `");
	}
}
