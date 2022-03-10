package com.xharlock.holo.commands.cmds;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to fetch a quote from {@link inspirobot.me} and display it in an embed
 */
public class InspiroCmd extends Command {

	private static final String apiUrl = "https://inspirobot.me/api?generate=true";

	public InspiroCmd(String name) {
		super(name);
		setDescription("Use this command to get a random quote from [Inspirobot](https://inspirobot.me/)");
		setUsage(name);
		setThumbnail("https://inspirobot.me/website/images/inspirobot-dark-green.png");
		setEmbedColor(new Color(35, 96, 19));
		setCommandCategory(CommandCategory.MISC);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();

		String url = null;

		try {
			url = HttpResponse.readLine(apiUrl);
		} catch (IOException ex) {
			builder.setTitle("API Error");
			builder.setDescription("Something went wrong! Please try again in a few minutes.");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		builder.setTitle("InspiroBot Quote");
		builder.setImage(url);
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true, embedColor);
	}
}