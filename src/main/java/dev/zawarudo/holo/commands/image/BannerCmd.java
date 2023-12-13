package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Command(name = "banner",
		description = "Retrieves the current banner of this guild. Note that a guild " +
				"needs to be boosted to level 2 in order to have a banner.",
		category = CommandCategory.IMAGE)
public class BannerCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		EmbedBuilder builder = new EmbedBuilder();
		String bannerUrl = event.getGuild().getBannerUrl();

		if (bannerUrl == null) {
			builder.setTitle("No Banner Found!");
			builder.setDescription("This guild doesn't seem to have a banner.");
			sendEmbed(event, builder, true, 15, TimeUnit.SECONDS);
		} else {
			builder.setTitle("Banner of " + event.getGuild().getName(), bannerUrl + "?size=4096");
			builder.setImage(bannerUrl + "?size=4096");
			sendEmbed(event, builder, true, 1, TimeUnit.MINUTES);
		}
	}
}