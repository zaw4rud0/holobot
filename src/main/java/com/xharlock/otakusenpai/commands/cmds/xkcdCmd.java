package com.xharlock.otakusenpai.commands.cmds;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class xkcdCmd extends Command {

	private int newest_issue;
	
	public xkcdCmd(String name) {
		super(name);
		setDescription("Use this command to access the comics of xkcd");
		setUsage(name + " [new|issue_nr]");
		setCommandCategory(CommandCategory.MISC);
		
		try {
			newest_issue = HttpResponse.getJsonObject("https://xkcd.com/info.0.json").get("num").getAsInt();
			System.out.println(newest_issue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();
		if (args.length == 0) {
			Random random = new Random();
			JsonObject object = null;
			try {
				object = HttpResponse.getJsonObject("https://xkcd.com/" + (random.nextInt(newest_issue) + 1) + "/info.0.json");
			} catch (IOException ex) {
				return;
			}
			builder.setTitle(object.get("title").getAsString() + " (xkcd #" + object.get("num").getAsInt() + ")");
			builder.setImage(object.get("img").getAsString());
			sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
		} else if (args.length != 1) {
			return;
		} else if (args[0].equals("new")) {
			JsonObject object = null;
			try {
				object = HttpResponse.getJsonObject("https://xkcd.com/info.0.json");
			} catch (IOException ex) {
				return;
			}
			builder.setTitle(object.get("title").getAsString() + " (xkcd #" + object.get("num").getAsInt() + ")");
			builder.setImage(object.get("img").getAsString());
			sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
		} else {
			int num;
			JsonObject object = null;
			try {
				num = Integer.parseInt(args[0]);
				
				if (num > newest_issue || num < 1)
					return;
				
				object = HttpResponse.getJsonObject("https://xkcd.com/" + num + "/info.0.json");
			} catch (NumberFormatException | IOException ex) {
				return;
			}
			builder.setTitle(object.get("title").getAsString() + " (xkcd #" + object.get("num").getAsInt() + ")");
			builder.addField("Date", object.get("day").getAsInt() + "/" + object.get("month").getAsInt() + "/" + object.get("year").getAsInt(), true);
			builder.setImage(object.get("img").getAsString());
			sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
		}
	}
}
