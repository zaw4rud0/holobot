package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Deactivated
@Command(name = "neko",
		description = "Sends a random catgirl (neko) image.",
		alias = {"catgirl", "kemonomimi"},
		isNSFW = true,
		category = CommandCategory.IMAGE)
public class NekoCmd extends AbstractCommand {

	private final String[] urls = {
			"https://nekos.life/api/v2/img/neko", 
			"https://neko-love.xyz/api/v1/neko",
			"https://nekos.life/api/v2/img/kemonomimi",
			"https://nekos.best/api/v1/nekos"
	};

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();
		String url;

		try {
			// Keeps fetching a new url until the url isn't on the blocklist
			do {
				url = HttpResponse.getJsonObject(urls[new Random().nextInt(urls.length)]).get("url").getAsString();
			} while (BlockCmd.blocked.contains(url));
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an image. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		builder.setTitle("Neko");
		builder.setImage(url);
		sendEmbed(e, builder, true);
	}
}