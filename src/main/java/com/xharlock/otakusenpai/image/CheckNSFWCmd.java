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
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CheckNSFWCmd extends Command {

	public CheckNSFWCmd(String name) {
		super(name);
		setDescription(
				"Use this command to evaluate the likelyhood of an image being NSFW. You may add the image as an attachment so no link is needed. Replying to a message containing a picture also works.");
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		e.getMessage().delete().queue();

		String oldUrl = "";
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {

			// If message is replying to another message
			if (e.getMessage().getReferencedMessage() != null) {

				// Message is embed, get image url
				if (!e.getMessage().getReferencedMessage().getEmbeds().isEmpty()) {
					oldUrl = e.getMessage().getReferencedMessage().getEmbeds().get(0).getImage().getUrl();
				}

				else {

				}
			}

			else {
				if (e.getMessage().getAttachments().size() == 0) {
					builder.setTitle("Incorrect Usage");
					builder.setDescription("Use `" + this.getGuildPrefix(e.getGuild())
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
				builder.setDescription("Use `" + this.getGuildPrefix(e.getGuild())
						+ "help check` to see the correct usage of this command");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}
			oldUrl = args[0].replace("<", "").replace(">", "");
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
