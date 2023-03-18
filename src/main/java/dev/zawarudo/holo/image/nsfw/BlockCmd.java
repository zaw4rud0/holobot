package dev.zawarudo.holo.image.nsfw;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.database.DBOperations;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "block",
		description = "Requests to block an image. Simply reply to a message containing " +
				"the image you want to block. Note that this command is intended for NSFW " +
				"(not safe for work) images and that you will be blacklisted if you abuse it.",
		category = CommandCategory.IMAGE)
public class BlockCmd extends AbstractCommand {

	public static List<String> blocked;	
	public static List<String> blockRequests;

	public BlockCmd() {
		// Get the blocked images from the DB
		try {
			blocked = DBOperations.getBlockedImages();
			//blockRequests = getBlockRequests();
			blockRequests = new ArrayList<>();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		// TODO Code part to call the block request list so that it can be reviewed by the owner

		EmbedBuilder builder = new EmbedBuilder();
		
		if (e.getMessage().getReferencedMessage() == null) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please reply to a message containing the image to block it");
			sendEmbed(e, builder, false, 15, TimeUnit.SECONDS);
			return;
		}
		
		// Bot owner can directly block the image
		if (isBotOwner(e.getAuthor())) {
			block(e);
		}
	}
	
	/**
	 * Effectively block the image, can only be done by the owner
	 */
	public void block(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();

		if (e.getMessage().getReferencedMessage() == null) {
			// TODO: Add error message
			return;
		}

		String url = getUrl(e.getMessage().getReferencedMessage());
		
		if (url == null) {
			builder.setTitle("Image not found");
			builder.setDescription("Please make sure the message you replied to contains an image");
			sendEmbed(e, builder, false, 15, TimeUnit.SECONDS);
			return;
		}

		try {
			DBOperations.insertBlockedImage(url, e.getAuthor().getIdLong(), e.getMessage().getTimeCreated().toString(), "None given");
		} catch (SQLException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong. Please try again later");
			sendEmbed(e, builder, false, 15, TimeUnit.SECONDS);
			return;
		}
		
		blocked.add(url);
		
		if (e.isFromGuild()) {
			e.getMessage().getReferencedMessage().delete().queue();
		}

		builder.setTitle("Image has been bonked");
		builder.setDescription("The image has been added to the blocklist and won't appear ever again");
		sendEmbed(e, builder, false, 15, TimeUnit.SECONDS);
	}
	
	/**
	 * Get the url from a referenced message
	 */
	// TODO: Use method of super class
	private String getUrl(Message msg) {
		String url = null;
		// Check if image is an attachment
		if (msg.getAttachments().size() != 0) {
			url = msg.getAttachments().get(0).getUrl();
		}
		// Check if image is in embed
		else if (msg.getEmbeds().size() > 0) {
			MessageEmbed embed = msg.getEmbeds().get(0);
			if (embed.getImage() != null) {
				url = embed.getImage().getUrl();
			}
		}
		// Image url is likely text of message
		else if (isValidUrl(msg.getContentRaw())) {
			url = msg.getContentRaw();
		}
		return url;
	}
}