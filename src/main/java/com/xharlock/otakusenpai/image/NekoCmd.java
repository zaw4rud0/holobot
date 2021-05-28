package com.xharlock.otakusenpai.image;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NekoCmd extends Command {

	private String[] URLs = { 
			"https://nekos.life/api/v2/img/neko",
			"https://neko-love.xyz/api/v1/neko",
			"https://nekos.life/api/v2/img/kemonomimi",
			//"http://api.nekos.fun:8080/api/neko" 
			};

	public NekoCmd(String name) {
		super(name);
		setDescription("Use this command to get a picture of a catgirl (neko)");
		setAliases(List.of("catgirl", "kemonomimi"));
		setUsage(name);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		Random rand = new Random();
		EmbedBuilder builder = new EmbedBuilder();
		String url = null;
		
		try {
			url = HttpResponse.getJsonObject(URLs[rand.nextInt(URLs.length)]).get("url").getAsString();
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong! Please try again in a few minutes.");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		builder.setTitle("Neko");
		builder.setImage(url);
		sendEmbed(e, builder, true);
	}
}
