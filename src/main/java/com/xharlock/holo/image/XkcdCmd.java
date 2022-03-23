package com.xharlock.holo.image;

import java.io.IOException;
import java.util.Random;

import com.google.gson.JsonObject;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class XkcdCmd extends Command {

	private int newestIssue;
	private static final String explainedUrl = "https://www.explainxkcd.com/";
	
	public XkcdCmd(String name) {
		super(name);
		setDescription("Use this command to access the comics of xkcd");
		setUsage(name + " [new|issue_nr]");
		setCommandCategory(CommandCategory.MISC);

		try {
			newestIssue = HttpResponse.getJsonObject("https://xkcd.com/info.0.json").get("num").getAsInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();
		
		// Random issue
		if (args.length == 0) {
			Random random = new Random();
			JsonObject object = null;
			try {
				object = HttpResponse.getJsonObject("https://xkcd.com/" + (random.nextInt(newestIssue) + 1) + "/info.0.json");
			} catch (IOException ex) {
				return;
			}
			
			int issueNr = object.get("num").getAsInt();
			builder.setTitle(object.get("title").getAsString() + " (xkcd #" + issueNr + ")");
			builder.setDescription("[Explained](" + explainedUrl + issueNr + ")");
			builder.setImage(object.get("img").getAsString());
		}
		
		// Newest issue
		else if (args[0].equals("new")) {
			JsonObject object = null;
			try {
				object = HttpResponse.getJsonObject("https://xkcd.com/info.0.json");
			} catch (IOException ex) {
				ex.printStackTrace();
				return;
			}
			
			int issueNr = object.get("num").getAsInt();
			builder.setTitle(object.get("title").getAsString() + " (xkcd #" + issueNr + ")");
			builder.setDescription("[Explained](" + explainedUrl + issueNr + ")");
			builder.setImage(object.get("img").getAsString());
			newestIssue = issueNr;
		} 
		
		// Specific issue
		else {
			int num;
			JsonObject object = null;
			try {
				num = Integer.parseInt(args[0]);
				if (num > newestIssue || num < 1) {
					return;
				}
				object = HttpResponse.getJsonObject("https://xkcd.com/" + num + "/info.0.json");
			} catch (NumberFormatException | IOException ex) {
				ex.printStackTrace();
				return;
			}
			
			int issueNr = object.get("num").getAsInt();
			builder.setTitle(object.get("title").getAsString() + " (xkcd #" + issueNr + ")");
			builder.setDescription("[Explained](" + explainedUrl + issueNr + ")");
			builder.addField("Date", object.get("day").getAsInt() + "/" + object.get("month").getAsInt() + "/" + object.get("year").getAsInt(), true);
			builder.setImage(object.get("img").getAsString());	
		}
		sendEmbed(e, builder, true);
	}
}