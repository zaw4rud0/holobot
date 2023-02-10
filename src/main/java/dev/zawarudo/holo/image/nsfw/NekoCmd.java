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
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Deactivated
@Command(name = "neko",
		description = "Sends a random catgirl (neko) image.",
		alias = {"catgirl", "kemonomimi"},
		category = CommandCategory.IMAGE)
public class NekoCmd extends AbstractCommand {

	private final String[] urls = {
			"https://nekos.best/api/v2/neko"
	};

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		EmbedBuilder builder = new EmbedBuilder();
		String url;

		try {
			do {
				url = HttpResponse.getJsonObject(urls[new Random().nextInt(urls.length)]).getAsJsonArray("results").get(0).getAsJsonObject().get("url").getAsString();
			} while (BlockCmd.blocked.contains(url));
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an image. Please try again in a few minutes!");
			sendEmbed(event, builder, true, 15, TimeUnit.SECONDS);
			return;
		}

		builder.setTitle("Neko");
		builder.setImage(url);
		sendEmbed(event, builder, true);
	}
}