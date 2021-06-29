package com.xharlock.holo.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CheckNSFWCmdOld extends Command {

	public CheckNSFWCmdOld(String name) {
		super(name);
		setDescription(
				"Use this command to evaluate the likelyhood of an image being NSFW. You may add the image as an attachment so no link is needed. Replying to a message containing a picture also works.");
		setUsage(name + " [image link]");
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		if (e.isFromGuild())
			e.getMessage().delete().queue();

		e.getChannel().sendTyping().queue();

		String oldUrl = null;
		Message referenced = null;
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {

			// If message is replying to another message
			if (e.getMessage().getReferencedMessage() != null) {

				referenced = e.getMessage().getReferencedMessage();

				// Message is embed, get image url from it
				if (!referenced.getEmbeds().isEmpty()) {

					if (referenced.getEmbeds().get(0).getImage() != null)
						oldUrl = referenced.getEmbeds().get(0).getImage().getUrl();
				}

				// No embed, try to get image
				else {
					if (!e.getMessage().getReferencedMessage().getAttachments().isEmpty()) {

						Attachment attachment = e.getMessage().getReferencedMessage().getAttachments().get(0);

						if (attachment.getFileExtension().equals("png") || attachment.getFileExtension().equals("jpg")
								|| attachment.getFileExtension().equals("gif")
								|| attachment.getFileExtension().equals("jpeg"))
							oldUrl = e.getMessage().getReferencedMessage().getAttachments().get(0).getUrl();

						else {
							builder.setTitle("Invalid Attachment");
							builder.setDescription("I only support images of the format `png`, `jpg`, `gif` or `jpeg`");
							sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
							return;
						}
					}

					else {
						for (String s : args) {
							if (isValidURL(s)) {
								oldUrl = s;
								break;
							}
						}
					}
				}
			}

			else {
				if (e.getMessage().getAttachments().size() == 0) {
					builder.setTitle("Incorrect Usage");
					builder.setDescription(
							"Use `" + getPrefix(e) + "help check` to see the correct usage of this command");
					sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
					return;
				}

				Attachment attachment = e.getMessage().getAttachments().get(0);
				if (!attachment.getFileExtension().equals("png") && !attachment.getFileExtension().equals("jpg")) {
					builder.setTitle("Invalid Attachment");
					builder.setDescription("Please provide an image of the format `png` or `jpg`");
					sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
					return;
				}
				oldUrl = attachment.getUrl();
			}
		} else {
			if (args.length != 1) {
				e.getMessage().delete().queue();
				builder.setTitle("Incorrect Usage");
				builder.setDescription("Use `" + getPrefix(e) + "help check` to see the correct usage of this command");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}
			oldUrl = args[0].replace("<", "").replace(">", "");
		}

		if (oldUrl == null) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Use `" + getPrefix(e) + "help check` to see the correct usage of this command");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		JsonObject obj = null;

		try {
			obj = getJsonObject(oldUrl);
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while communicating with the API");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		System.out.println(obj.toString());
		double score = obj.getAsJsonObject("output").get("nsfw_score").getAsDouble();
		
		if (args.length == 2 && args[1].equals("advanced")) {
			builder.setTitle("Advanced NSFW Check");
			
			BufferedImage img = null;
			
			try {
				img = ImageIO.read(new URL(oldUrl));
			} catch (IOException ex) {
				ex.printStackTrace();
				return;
			}
			
			int boxes = obj.getAsJsonObject("output").getAsJsonArray("detections").size();
			
			for (int i = 0; i < boxes; i++) {
				JsonObject detection = obj.getAsJsonObject("output").getAsJsonArray("detections").get(i).getAsJsonObject();
				JsonArray bounding_box = detection.getAsJsonArray("bounding_box");
				builder.addField("Box " + (i + 1), "**Reason:** " + detection.get("name").getAsString() + "\n"
						+ "**Confidence:** " + detection.get("confidence").getAsDouble(), false);
				img = drawBox(img, bounding_box.get(0).getAsInt(), bounding_box.get(1).getAsInt(), bounding_box.get(2).getAsInt(), bounding_box.get(3).getAsInt(), (i + 1));
			}
		}

		else {
			String scoreString = "`" + String.format("%.2f", score * 100.0) + "%`";
			builder.setTitle("NSFW Check");
			builder.setDescription("Your image is " + scoreString + " likely to contain NSFW elements");
		}
		
		if (referenced != null)
			sendReplyEmbed(e, builder, 1, TimeUnit.MINUTES, true);
		else
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
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

	// Get more informations about nsfw elements in the image
	private static JsonObject getJsonObject(String imageUrl) throws IOException {
		String url = "https://api.deepai.org/api/nsfw-detector";
		String token = Bootstrap.holo.getConfig().getKeyDeepAI();
		Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + url);
		String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines()
				.collect(Collectors.joining("\n"));
		return JsonParser.parseString(result).getAsJsonObject();
	}

}
