package com.xharlock.otakusenpai.commands.cmds;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.utils.JSONReader;
import com.xharlock.otakusenpai.utils.JSONWriter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SuggestionCmd extends Command {

	private final String filepath = "./src/main/resources/misc/suggestions.json";
	
	public SuggestionCmd(String name) {
		super(name);
		setDescription("Use this command if you want to suggest a feature. Suggestions are always appreciated");
		setUsage(name + " <text>");
		setExample(name + " make this bot more awesome :D");
		setAliases(List.of("suggest"));
		setCommandCategory(CommandCategory.GENERAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();

		if (this.getArgs().length == 0) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please provide a description of your suggestion");
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
		if (e.isFromGuild())
			author.put("Nickname", e.getMember().getEffectiveName());
		obj.put("Author", author);
		obj.put("Suggestion", text);

		try {
			JSONArray array = JSONReader.readJSONArray(filepath);
			array.add(obj);
			JSONWriter.writeJSONArray(array, filepath);
		} catch (IOException | ParseException e1) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong! Please try again in a few minutes.");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		builder.setTitle("Message Sent");
		builder.setDescription("Thank you for your suggestion!");
		this.sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
	}

}
