package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "banner",
		description = "Retrieves the current banner of this guild. A guild needs to be boosted to level 2 in order to have a banner.",
		category = CommandCategory.IMAGE)
public class BannerCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();		
		
		if (e.getGuild().getBannerUrl() == null) {
			builder.setTitle("No Banner Found!");
			builder.setDescription("This guild doesn't seem to have a banner.");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
		} else {
			builder.setTitle("Banner of " + e.getGuild().getName(), e.getGuild().getBannerUrl() + "?size=4096");
			builder.setImage(e.getGuild().getBannerUrl() + "?size=4096");
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
		}
	}
}