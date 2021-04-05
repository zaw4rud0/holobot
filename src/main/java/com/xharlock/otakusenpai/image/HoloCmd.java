package com.xharlock.otakusenpai.image;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.misc.Messages;
import com.xharlock.otakusenpai.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HoloCmd extends Command {

	private final String api_url = "https://nekos.life/api/v2/img/holo";
	
	public HoloCmd(String name) {
		super(name);
		setDescription("Use this command to get a picture of Holo");
		setAliases(List.of("bestgirl", "waifu", "wisewolf"));
		setUsage(name);
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();
		
		String url = null;
		
		try {
			url = HttpResponse.getJsonObject(api_url).get("url").getAsString();
		} catch (IOException ex) {
			builder.setTitle(Messages.TITLE_ERROR.getText());
			builder.setDescription("Something went wrong! Please try again in a few minutes.");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		builder.setTitle("Holo");
		builder.setImage(url);
		sendEmbed(e, builder, true);
	}

}
