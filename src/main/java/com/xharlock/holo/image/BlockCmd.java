package com.xharlock.holo.image;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.database.DatabaseOPs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BlockCmd extends Command {

	public static List<String> blocked;	
	public static List<String> blockRequests;

	public BlockCmd(String name) {
		super(name);
		setDescription("Use this command to request a block of an image. Simply reply to a message containing the image. Note that this command is intended for NSFW images, and that you will be blacklisted if you abuse it.");
		setUsage(name);
		setAliases(List.of("bonk"));
		setCommandCategory(CommandCategory.IMAGE);
		
		// Get the blocked images from the DB
		try {
			blocked = DatabaseOPs.getBlockedImages();
			blockRequests = DatabaseOPs.getBlockRequests();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		// TODO Code part to call the block request list so that it can be reviewed by the owner

		EmbedBuilder builder = new EmbedBuilder();
		
		if (e.getMessage().getReferencedMessage() == null) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please reply to a message containing the image to block it");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		// Bot owner can directly block the image
		if (isBotOwner(e)) {
			block(e);
		}
		
		// Everyone else can only request a block
		else {
			requestBlock(e);
		}
	}
	
	/**
	 * Effectively block the image, can only be done by the owner
	 */
	public void block(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();
		
		String url = getUrl(e.getMessage().getReferencedMessage());
		
		if (url == null) {
			builder.setTitle("Image not found");
			builder.setDescription("Please make sure the message you replied to contains an image");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
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
		
		if (e.isFromGuild()) {
			e.getMessage().getReferencedMessage().delete().queue();
		}

		builder.setTitle("Image has been bonked");
		builder.setDescription("The image has been added to the blocklist and won't appear ever again");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}
	
	/**
	 * Adds it to the list of requested blocks
	 */
	public void requestBlock(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();

		String url = getUrl(e.getMessage().getReferencedMessage());
		
		if (url == null) {
			builder.setTitle("Image not found");
			builder.setDescription("Please make sure the message you replied to contains an image");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		try {
			DatabaseOPs.addBlockRequest(url, e.getAuthor(), e.getMessage().getTimeCreated().toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong. Please try again later");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		blockRequests.add(url);
		
		deleteInvoke(e);

		builder.setTitle("Block request has been sent");
		builder.setDescription("The block request will be reviewed as soon as possible");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}
	
	/**
	 * TODO: Method to view the block requests
	 */
	@SuppressWarnings("unused")
	private void viewRequests() {
		System.out.println("This method is not implemented yet!");
	}
	
	/**
	 * Method to get the url from a referenced message
	 */
	private String getUrl(Message msg) {
		String url = null;
		
		// Check if image is an attachment
		if (msg.getAttachments().size() != 0) {
			url = msg.getAttachments().get(0).getUrl();
		}

		// Check if image is in embed
		else if (msg.getEmbeds().size() > 0) {
			url = msg.getEmbeds().get(0).getImage().getUrl();
		}

		// Image url is likely text of message
		else if (isValidURL(msg.getContentRaw())) {
			url = msg.getContentRaw();
		}
		
		return url;
	}
}
