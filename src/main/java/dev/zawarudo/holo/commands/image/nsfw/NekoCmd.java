package dev.zawarudo.holo.commands.image.nsfw;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

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
		BlockCmd blockCmd = (BlockCmd) Bootstrap.holo.getCommandManager().getCommand("block");

		deleteInvoke(event);

		EmbedBuilder builder = new EmbedBuilder();
		String url;

		try {
			do {
				url = HttpResponse.getJsonObject(urls[new Random().nextInt(urls.length)]).getAsJsonArray("results").get(0).getAsJsonObject().get("url").getAsString();
			} while (blockCmd.isBlocked(url) || blockCmd.isBlockRequested(url));
		} catch (IOException ex) {
			sendErrorEmbed(event, "Something went wrong while fetching an image. Please try again in a few minutes!");
			return;
		}

		builder.setTitle("Neko");
		builder.setImage(url);
		sendEmbed(event, builder, true);
	}
}