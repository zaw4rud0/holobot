package com.xharlock.holo.image;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BannerCmd extends Command {

	public BannerCmd(String name) {
		super(name);
		setDescription("Use this command to get the current banner of this guild. A guild needs to be boosted to Level 2 in order to have a banner set.");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.IMAGE);
	}

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