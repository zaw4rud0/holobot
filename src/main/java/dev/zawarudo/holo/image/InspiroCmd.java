package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.holo.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Command to fetch a quote from inspirobot.me and display it in an embed.
 */
@Command(name = "inspiro",
		description = "Fetch a random quote from [Inspirobot](https://inspirobot.me).",
		thumbnail = "https://inspirobot.me/website/images/inspirobot-dark-green.png",
		embedColor = EmbedColor.INSPIRO,
		guildOnly = false,
		category = CommandCategory.IMAGE)
public class InspiroCmd extends AbstractCommand {

	private static final String API_URL = "https://inspirobot.me/api?generate=true";

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);
		EmbedBuilder builder = new EmbedBuilder();

		String url;
		try {
			url = HttpResponse.readLine(API_URL);
		} catch (IOException ex) {
			sendErrorEmbed(event, "Something went wrong while communicating with the API! Please try again in a few minutes.");
			return;
		}
		builder.setTitle("InspiroBot Quote");
		builder.setImage(url);
		sendEmbed(event, builder, true, 5, TimeUnit.MINUTES, getEmbedColor());
	}
}