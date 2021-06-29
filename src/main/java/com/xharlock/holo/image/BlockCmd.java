package com.xharlock.holo.image;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.database.DatabaseOPs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BlockCmd extends Command {

	public static List<String> blocked;

	public BlockCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to block an image. Simply reply to a message containing the image.");
		setUsage(name);
		setAliases(List.of("bonk"));
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
		
		// Get the blocked images from the DB
		try {
			blocked = DatabaseOPs.getBlockedImages();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();

		EmbedBuilder builder = new EmbedBuilder();

		if (e.getMessage().getReferencedMessage() == null) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please reply to a message containing the image to block it");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		String url = "";

		// Check if image is an attachment
		if (e.getMessage().getReferencedMessage().getAttachments().size() != 0) {
			url = e.getMessage().getReferencedMessage().getAttachments().get(0).getUrl();
		}

		// Check if image is in a embed
		else if (e.getMessage().getReferencedMessage().getEmbeds().size() > 0) {
			url = e.getMessage().getReferencedMessage().getEmbeds().get(0).getImage().getUrl();
		}

		// Image url is most likely just the text of the message
		else {
			url = e.getMessage().getContentRaw();
		}

		try {
			DatabaseOPs.addBlockedImage(url, e.getAuthor(), e.getMessage().getTimeCreated().toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong. Please try again later");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		blocked.add(url);
		
		if (e.isFromGuild())
			e.getMessage().getReferencedMessage().delete().queue();

		builder.setTitle("Image Blocked");
		builder.setDescription("The image has been added to the blocklist and won't appear ever again");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}
}
