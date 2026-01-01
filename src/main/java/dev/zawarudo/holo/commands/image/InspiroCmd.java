package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.utils.HoloHttp;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.utils.exceptions.HttpStatusException;
import dev.zawarudo.holo.utils.exceptions.HttpTransportException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Command to fetch a quote from inspirobot.me and display it in an embed.
 */
@CommandInfo(name = "inspiro",
		description = "Fetch a random quote from [InspiroBot](https://inspirobot.me).",
		thumbnail = "https://inspirobot.me/website/images/inspirobot-dark-green.png",
		embedColor = EmbedColor.INSPIRO,
		guildOnly = false,
		category = CommandCategory.IMAGE)
public class InspiroCmd extends AbstractCommand {

	private static final String API_URL = "https://inspirobot.me/api?generate=true";

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		String imageUrl;
		try {
			imageUrl = HoloHttp.readLine(API_URL);
		} catch (HttpStatusException ex) {
			sendErrorEmbed(event, "InspiroBot returned an error (HTTP " + ex.getStatusCode() + "). Please try again later.");
			return;
		} catch (HttpTransportException ex) {
			sendErrorEmbed(event, "I couldn't reach InspiroBot. Please try again in a few minutes.");
			return;
		}

		if (imageUrl.isBlank()) {
			sendErrorEmbed(event, "InspiroBot returned an empty response. Please try again.");
			return;
		}

        EmbedBuilder builder = new EmbedBuilder()
				.setTitle("InspiroBot Quote")
				.setImage(imageUrl);

		sendEmbed(event, builder, true, 5, TimeUnit.MINUTES, getEmbedColor());
	}
}