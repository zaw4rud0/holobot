package com.xharlock.holo.image;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HoloCmd extends Command {

	private final String api_url = "https://nekos.life/api/v2/img/holo";
	
	public HoloCmd(String name) {
		super(name);
		setDescription("Use this command to get a picture of Holo");
		setAliases(List.of("bestgirl", "waifu", "wisewolf"));
		setUsage(name);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		String url = null;
		
		try {
			// Keeps fetching a new url until the url isn't on the blocklist
			do {
				url = HttpResponse.getJsonObject(api_url).get("url").getAsString();
			} while (BlockCmd.blocked.contains(url));
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
