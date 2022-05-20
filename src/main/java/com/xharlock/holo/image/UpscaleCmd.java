package com.xharlock.holo.image;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Command(name = "upscale",
		description = "Upscales a given image with Waifu2x. Please provide an image as an attachment " +
					  "or a link to process it. Alternatively, you can reply to a message with an image.",
		embedColor = EmbedColor.NONE,
		category = CommandCategory.IMAGE)
public class UpscaleCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder eb = new EmbedBuilder();

		Message referenced = e.getMessage().getReferencedMessage();
		String url = referenced != null ? getImage(referenced) : getImage(e.getMessage());

		// User didn't provide an image
		if (url == null) {
			eb.setTitle("Error");
			eb.setDescription("You need to provide an image to upscale!");
			sendEmbed(e, eb, 30, TimeUnit.SECONDS, true, getEmbedColor());
			return;
		}

		sendTyping(e);

		try {
			url = process(url);
		} catch (IOException ex) {
			eb.setTitle("Error");
			eb.setDescription("Something went wrong while processing your image! Please try again later.");
			sendEmbed(e, eb, 30, TimeUnit.SECONDS, true, getEmbedColor());
			return;
		}

		eb.setTitle("Upscaled Image");
		eb.setImage(url);
		sendEmbed(e, eb, 5, TimeUnit.MINUTES, true, getEmbedColor());
	}

	/**
	 * The URL of the Waifu2x API.
	 */
	public static final String apiUrl = "https://api.deepai.org/api/waifu2x";

	/**
	 * Sends a given image URL to the Waifu2x API where it is upscaled. The processed image is then returned.
	 *
	 * @param url The URL of the image to upscale.
	 * @return The URL of the upscaled image.
	 */
	public static String process(String url) throws IOException {
		String token = Bootstrap.holo.getConfig().getKeyDeepAI();
		Process pr = Runtime.getRuntime().exec("curl -F image=" + url + " -H api-key:" + token + " " + apiUrl);
		BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String result = reader.lines().collect(Collectors.joining("\n"));
		JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
		if (obj == null || obj.get("err") != null) {
			throw new IOException("No result!");
		}
		return obj.get("output_url").getAsString();
	}
}