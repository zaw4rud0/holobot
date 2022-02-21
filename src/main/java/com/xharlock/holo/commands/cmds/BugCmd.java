package com.xharlock.holo.commands.cmds;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.JSONReader;
import com.xharlock.holo.utils.JSONWriter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BugCmd extends Command {

	private static final String filepath = "./src/main/resources/misc/bugs.json";

	public BugCmd(String name) {
		super(name);
		setDescription("Use this command to report a bug. Please provide a description of the bug and how it happened");
		setUsage(name + " <text>");
		setExample(name + " Something went wrong");
		setCommandCategory(CommandCategory.GENERAL);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please provide a description of the bug");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		String text = String.join(" ", getArgs());
		JSONObject obj = new JSONObject();

		obj.put("Date", e.getMessage().getTimeCreated().toString());

		if (e.isFromGuild()) {
			JSONObject guild = new JSONObject();
			guild.put("Guild Id", e.getGuild().getIdLong());
			guild.put("Guild Name", e.getGuild().getName());
			guild.put("Channel Id", e.getChannel().getId());
			obj.put("Guild", guild);
		} else {
			obj.put("Private Channel", e.getPrivateChannel().getIdLong());
		}

		JSONObject author = new JSONObject();
		author.put("Id", e.getAuthor().getIdLong());
		author.put("Tag", e.getAuthor().getAsTag());
		if (e.isFromGuild()) {
			author.put("Nickname", e.getMember().getEffectiveName());
		}
		obj.put("Author", author);
		obj.put("Bug", text);

		try {
			JSONArray array = JSONReader.readJSONArray(filepath);
			array.add(obj);
			JSONWriter.writeJSONArray(array, filepath);
		} catch (IOException | ParseException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong! Please try again in a few minutes.");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		builder.setTitle("Message Sent");
		builder.setDescription("Thank you for reporting the bug!");
		sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
	}
}
