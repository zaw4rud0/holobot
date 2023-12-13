package dev.zawarudo.holo.commands.image.nsfw;

import dev.zawarudo.danbooru.DanbooruAPI;
import dev.zawarudo.danbooru.DanbooruPost;
import dev.zawarudo.exceptions.APIException;
import dev.zawarudo.exceptions.InvalidRequestException;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.utils.BooruAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		} catch (SQLException ex) {
			if (logger.isErrorEnabled()) {
				logger.error("Something went wrong while fetching the waifus from the DB.", ex);
			}
		}
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		BlockCmd blockCmd = (BlockCmd) Bootstrap.holo.getCommandManager().getCommand("block");

		deleteInvoke(event);
		
		EmbedBuilder builder = new EmbedBuilder();
		
		// Owner added a new waifu to the DB
		if (isBotOwner(event.getAuthor()) && args.length >= 4 && args[0].equals("add")) {
			String name = args[1];
			String tag = args[2];
			String title = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
			
			builder.setTitle("Waifu added to the database");
			String description = "Name: %s\nTag: %s\nTitle: %s";
			builder.setDescription(String.format(description, name, tag, title));
			
			try {
				DBOperations.insertWaifu(name, tag, title);
			} catch (SQLException ex) {
				sendErrorEmbed(event, "Something went wrong while adding a new waifu to the database. Please try again in a few minutes.");
				if (logger.isErrorEnabled()) {
					logger.error("Something went wrong while adding a new waifu to the database.", ex);
				}
				return;
			}
			
			sendEmbed(event, builder, true, 1, TimeUnit.MINUTES, getEmbedColor());
			names.add(name);
			Collections.sort(names);
			return;
		}
		
		// Display all available tags
		if (args.length == 0 || args[0].toLowerCase(Locale.UK).equals("list")) {
			builder.setTitle("Image Tags");
			builder.setDescription(getCategoriesString());
			builder.addField("Usage", "`" + getPrefix(event) + "image <tag>`", false);
			sendEmbed(event, builder, true, getEmbedColor());
			return;
		}
		
		// Tag not found
		if (!names.contains(args[0].toLowerCase(Locale.UK))) {
			builder.setTitle("Tag not found");
			builder.setDescription("Use `" + getPrefix(event) + "image` to see all available tags");
			sendEmbed(event, builder, true, 30, TimeUnit.SECONDS, getEmbedColor());
			return;
		}
	
		String name = args[0].toLowerCase(Locale.UK);
		
		try (ResultSet rs = DBOperations.getWaifu(name)) {
			rs.next();
			String tag = rs.getString("Tag");
			String url;
			
			// Keeps fetching a new url until the url isn't on the blocklist
			do {
				url = getImage(tag);
			} while (blockCmd.isBlocked(url) || blockCmd.isBlockRequested(url));
			
			builder.setTitle(rs.getString("Title"));
			builder.setImage(url);
		} catch (SQLException | APIException | InvalidRequestException ex) {
			sendErrorEmbed(event, "Something went wrong while fetching an image. Please try again in a few minutes!");
			if (logger.isErrorEnabled()) {
				logger.error("Something went wrong while fetching a waifu image.", ex);
			}
			return;
		}
		
		sendEmbed(event, builder, true, getEmbedColor());
	}
	
	/**
	 * Get the right image from Gelbooru
	 */
	@Nullable
	private String getImage(String tag) throws APIException, InvalidRequestException {
		List<DanbooruPost> posts = new DanbooruAPI()
				.setRating(BooruAPI.Rating.SAFE)
				.setOrder(BooruAPI.Order.RANDOM)
				.setLimit(1)
				.setTags(tag)
				.getPosts();
		if (posts.size() == 0) {
			return null;
		}
		return posts.get(0).getUrl();
	}

	/**
	 * Properly display all available tags as a single String.
	 */
	private String getCategoriesString() {
		return String.format("```%s```", String.join(", ", names));
	}
}