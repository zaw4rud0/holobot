package com.xharlock.holo.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.utils.BufferedImageOps;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CheckNSFWCmd extends Command {

	public CheckNSFWCmd(String name) {
		super(name);
		setDescription("Use this command to evaluate the likelyhood of an image being NSFW. You may add the image as an attachment so no link is needed. Replying to a message containing a picture also works."
				+ "\nTo get more informations on the evaluation, use `advanced` (or `adv` for short) as an additional argument.");
		setUsage(name + " [advanced|adv] [image_link]");
		setThumbnail("https://cdn.discordapp.com/attachments/862371045142429756/862371109629198346/nsfw_check.png");
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();

		e.getChannel().sendTyping().queue();

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(getColor(e));
		if (e.isFromGuild())
			builder.setFooter("Invoked by " + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());

		Message referenced = e.getMessage().getReferencedMessage();
		String old_url = referenced != null ? getImageUrl(referenced) : getImageUrl(e.getMessage());
		
		// Most likely incorrect usage
		if (old_url == null) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Use `" + getPrefix(e) + "help check` to see the correct usage of this command");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		JsonObject obj = null;

		try {
			obj = getJsonObject(old_url);
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while communicating with the API");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		// Advanced Check
		if (args.length >= 1 && (args[0].equals("advanced") || args[0].equals("adv"))) {
			
			try {
				double score = obj.getAsJsonObject("output").get("nsfw_score").getAsDouble();				
				String scoreString = "`" + String.format("%.2f", score * 100.0) + "%`";
				
				builder.setTitle("Advanced NSFW Check");
				builder.setDescription("Your image is " + scoreString + " likely to contain NSFW elements");
				builder.setImage("attachment://check.png");
				
				// Get the url as a BufferedImage to draw the boxes into it
				BufferedImage img = ImageIO.read(new URL(old_url));
				
				int boxes = obj.getAsJsonObject("output").getAsJsonArray("detections").size();
				
				// Draw the nsfw boxes
				for (int i = 0; i < boxes; i++) {
					JsonObject detection = obj.getAsJsonObject("output").getAsJsonArray("detections").get(i).getAsJsonObject();
					JsonArray bounding_box = detection.getAsJsonArray("bounding_box");
					
					// Values of the box
					int x = bounding_box.get(0).getAsInt();
					int y = bounding_box.get(1).getAsInt();
					int width = bounding_box.get(2).getAsInt();
					int height = bounding_box.get(3).getAsInt();
					
					// Draw the box into the image
					img = drawBox(img, x, y, width, height, (i + 1));
					
					// Display box informations 
					builder.addField("Box " + (i + 1), 
							"**Reason:** " + detection.get("name").getAsString() + "\n"
							+ "**Confidence:** " + detection.get("confidence").getAsDouble(), false);
				}
				
				InputStream input = BufferedImageOps.toInputStream(img);
				
				if (referenced != null)
					referenced.reply(input, "check.png").setEmbeds(builder.build()).queue();
				else
					e.getChannel().sendFile(input, "check.png").setEmbeds(builder.build()).queue();
			} 
			
			// Something went wrong
			catch (IOException ex) {
				ex.printStackTrace();
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while processing and evaluating your image. Please try again in a few minutes!");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
				return;
			}			
		}

		// Normal Check
		else {
			double score = obj.getAsJsonObject("output").get("nsfw_score").getAsDouble();
			String scoreString = "`" + String.format("%.2f", score * 100.0) + "%`";
			
			builder.setTitle("NSFW Check");
			builder.setDescription("Your image is " + scoreString + " likely to contain NSFW elements");

			if (referenced != null)
				referenced.replyEmbeds(builder.build()).queue();
			else
				e.getChannel().sendMessageEmbeds(builder.build()).queue();
		}
	}

	/**
	 * Method to get the image url out of a message (embed, attachment, etc.)
	 */
	private static String getImageUrl(Message msg) {
		String url = null;
		
		// Embed Image
		if (msg.getEmbeds().size() != 0) {
			if (msg.getEmbeds().get(0).getImage() != null)
				url = msg.getEmbeds().get(0).getImage().getUrl();
		}
		
		// Attachment Image
		else if (msg.getAttachments().size() != 0) {
			if (msg.getAttachments().get(0).isImage())
				url = msg.getAttachments().get(0).getUrl();
		}
		
		// Url Image
		else {
			url = msg.getContentRaw();
		}
		return url;
	}

	/**
	 * Method to draw a box into the image with the given properties
	 */
	private static BufferedImage drawBox(BufferedImage img, int x, int y, int width, int height, int box_number) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(5));
		g2d.drawRect(x, y, width, height);
		g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
		g2d.drawString("" + box_number, x + 12, y + 30);
		g2d.dispose();
		return img;
	}

	/**
	 * Method that makes an API request to evaluate the image
	 */
	private static JsonObject getJsonObject(String imageUrl) throws IOException {
		String url = "https://api.deepai.org/api/nsfw-detector";
		String token = Bootstrap.holo.getConfig().getKeyDeepAI();
		Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + url);
		String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines().collect(Collectors.joining("\n"));
		return JsonParser.parseString(result).getAsJsonObject();
	}

}
