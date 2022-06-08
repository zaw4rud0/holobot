package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.holo.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Command to fetch a quote from inspirobot.me and display it in an embed
 */
@Command(name = "inspiro",
		description = "Fetch a quote from [Inspirobot](https://inspirobot.me).",
		thumbnail = "https://inspirobot.me/website/images/inspirobot-dark-green.png",
		embedColor = EmbedColor.INSPIRO,
		guildOnly = false,
		category = CommandCategory.IMAGE)
public class InspiroCmd extends AbstractCommand {

	private static final String apiUrl = "https://inspirobot.me/api?generate=true";

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();

		String url;

		try {
			url = HttpResponse.readLine(apiUrl);
		} catch (IOException ex) {
			builder.setTitle("API Error");
			builder.setDescription("Something went wrong! Please try again in a few minutes.");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
			return;
		}

		builder.setTitle("InspiroBot Quote");
		builder.setImage(url);
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true, getEmbedColor());
	}
}