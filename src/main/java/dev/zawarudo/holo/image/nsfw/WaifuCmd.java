package dev.zawarudo.holo.image.nsfw;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.image.nsfw.BlockCmd;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.apis.GelbooruAPI;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.misc.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
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
		category = CommandCategory.IMAGE)
public class WaifuCmd extends AbstractCommand {

	private List<String> names;

	public WaifuCmd() {
		try {
			names = DBOperations.getWaifuNames();
			Collections.sort(names);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		
		// Owner added a new waifu to the DB
		if (isBotOwner(e.getAuthor()) && args.length >= 4 && args[0].equals("add")) {
			String name = args[1];
			String tag = args[2];
			String title = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
			
			builder.setTitle("Waifu added to the database");
			String description = "Name: %s\nTag: %s\nTitle: %s";
			builder.setDescription(String.format(description, name, tag, title));
			
			try {
				DBOperations.insertWaifu(name, tag, title);
			} catch (SQLException ex) {
				ex.printStackTrace();
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while adding a new waifu to the database. Please try again in a few minutes.");
			}
			
			sendEmbed(e, builder, true, 1, TimeUnit.MINUTES, getEmbedColor());
			names.add(name);
			Collections.sort(names);
			return;
		}
		
		// Display all available tags
		if (args.length == 0 || args[0].toLowerCase(Locale.UK).equals("list")) {
			builder.setTitle("Image Tags");
			builder.setDescription(getCategoriesString());
			builder.addField("Usage", "`" + getPrefix(e) + "image <tag>`", false);
			sendEmbed(e, builder, true, getEmbedColor());
			return;
		}
		
		// Tag not found
		if (!names.contains(args[0].toLowerCase(Locale.UK))) {
			builder.setTitle("Tag not found");
			builder.setDescription("Use `" + getPrefix(e) + "image` to see all available tags");
			sendEmbed(e, builder, true, 30, TimeUnit.SECONDS, getEmbedColor());
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
		
		sendEmbed(e, builder, true, getEmbedColor());
	}
	
	/**
	 * Get the right image from Gelbooru
	 */
	@Nullable
	private String getImage(String tag) throws IOException {
		JsonArray array = GelbooruAPI.getJsonArray(GelbooruAPI.Rating.GENERAL, GelbooruAPI.Sort.RANDOM, 1, tag);
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