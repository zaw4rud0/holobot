package com.xharlock.holo.commands.cmds;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MockCmd extends Command {

	public MockCmd(String name) {
		super(name);
		setDescription("Use this command to transform a text into the mocking form. You can also use this command while replying to a message to transform it.");
		setUsage(name + " <text>");
		setCommandCategory(CommandCategory.MISC);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		// User replied to a message
		if (e.getMessage().getReferencedMessage() != null) {
			String mock = mockify(e.getMessage().getReferencedMessage().getContentRaw().split(" "));
			if (mock.length() > 2000) {
				e.getChannel().sendMessage(mock.substring(0, 2000)).queue();
				e.getChannel().sendMessage(mock.substring(2000, mock.length())).queue();
			} else {
				e.getChannel().sendMessage(mock).queue();
			}
		}

		// User didn't provide text or message
		else if (args.length == 0) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Error");
			builder.setDescription("Please provide text or reply to a message!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
		}

		// Text given by user
		else {
			String mock = mockify(args);
			if (mock.length() > 2000) {
				e.getChannel().sendMessage(mock.substring(0, 2000)).queue();
				e.getChannel().sendMessage(mock.substring(2000, mock.length())).queue();
			} else {
				e.getChannel().sendMessage(mock).queue();
			}
		}
	}
	
	private String mockify(String[] raw) {
		String result = "";
		boolean lower = true;
		for (String s : String.join(" ", raw).split("")) {
			if (s.equals(" ")) {
				result += s;
			} else if (lower) {
				result += s.toLowerCase(Locale.UK);
				lower = false;
			} else {
				result += s.toUpperCase(Locale.UK);
				lower = true;
			}
		}
		return result;
	}
}