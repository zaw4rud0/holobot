package dev.zawarudo.holo.image.nsfw;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Deactivated
@Command(name = "holo",
		description = "Sends a random image of Holo from Spice & Wolf.",
		alias = {"bestgirl", "waifu", "wisewolf"},
		category = CommandCategory.IMAGE)
public class HoloCmd extends AbstractCommand {

	private static final String API_URL = "https://nekos.life/api/v2/img/holo";

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);
		
		EmbedBuilder builder = new EmbedBuilder();
		String url;
		
		try {
			do {
				System.out.println(HttpResponse.getJsonObject(API_URL));
				url = HttpResponse.getJsonObject(API_URL).get("url").getAsString();
			} while (BlockCmd.blocked.contains(url) || BlockCmd.blockRequests.contains(url));
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an image. Please try again in a few minutes!");
			sendEmbed(event, builder, true, 15, TimeUnit.SECONDS);
			return;
		}
		
		builder.setTitle("Holo");
		builder.setImage(url);
		sendEmbed(event, builder, true);
	}
}