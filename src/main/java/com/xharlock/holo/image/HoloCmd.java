package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Deactivated
@Command(name = "holo",
		description = "Sends a random image of Holo from Spice & Wolf.",
		alias = {"bestgirl", "waifu", "wisewolf"},
		isNSFW = true,
		category = CommandCategory.IMAGE)
public class HoloCmd extends AbstractCommand {

	private static final String apiUrl = "https://nekos.life/api/v2/img/holo";

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		String url;
		
		try {
			// Keeps fetching a new url until url isn't on blocklist
			do {
				url = HttpResponse.getJsonObject(apiUrl).get("url").getAsString();
			} while (BlockCmd.blocked.contains(url) || BlockCmd.blockRequests.contains(url));
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an image. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		builder.setTitle("Holo");
		builder.setImage(url);
		sendEmbed(e, builder, true);
	}
}