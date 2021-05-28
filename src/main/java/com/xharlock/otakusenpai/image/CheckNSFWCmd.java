package com.xharlock.otakusenpai.image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.core.Bootstrap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CheckNSFWCmd extends Command {

	public CheckNSFWCmd(String name) {
		super(name);
		setDescription(
				"Use this command to evaluate the likelyhood of an image being NSFW. You may add the image as an attachment so no link is needed. Replying to a message containing a picture also works.");
		setUsage(name + " [image link]");
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		// TODO In dire need of a cleanup :hyperkekw:		
		
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
					builder.setDescription("Use `" + getPrefix(e)
							+ "help check` to see the correct usage of this command");
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
				builder.setDescription("Use `" + getPrefix(e)
						+ "help check` to see the correct usage of this command");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}
			oldUrl = args[0].replace("<", "").replace(">", "");
		}

		if (oldUrl == null) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Use `" + getPrefix(e)
					+ "help check` to see the correct usage of this command");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		double score = 0.0;

		try {
			score = getNSFWScore(oldUrl);
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while communicating with the API");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		String scoreString = "`" + String.format("%.2f", score * 100.0) + "%`";

		builder.setTitle("NSFW Check");
		builder.setDescription("Your image is " + scoreString + " likely to contain NSFW elements");
		
		if (referenced != null)
			sendReplyEmbed(e, builder, 1, TimeUnit.MINUTES, true);
		else
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}

	private static final String url = "https://api.deepai.org/api/nsfw-detector";

	private static double getNSFWScore(String imageUrl) throws IOException {
		String token = Bootstrap.otakuSenpai.getConfig().getKeyDeepAI();
		Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + url);
		String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines()
				.collect(Collectors.joining("\n"));
		JsonObject object = JsonParser.parseString(result).getAsJsonObject();
		return object.getAsJsonObject("output").get("nsfw_score").getAsDouble();
	}

	// Get more informations about nsfw elements in the image
	@SuppressWarnings("unused")
	private static JsonObject getJsonObject(String imageUrl) throws IOException {
		String token = Bootstrap.otakuSenpai.getConfig().getKeyDeepAI();
		Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + url);
		String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines()
				.collect(Collectors.joining("\n"));
		return JsonParser.parseString(result).getAsJsonObject();
	}

}
