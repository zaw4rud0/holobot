package com.xharlock.holo.commands.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UwuCmd extends Command {

	public UwuCmd(String name) {
		super(name);
		setDescription("Use this command to translate a sentence into the holy and sacred UwU language. You can also use this command while replying to a message to translate it.");
		setUsage(name + " <text>");
		setCommandCategory(CommandCategory.MISC);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		// User replied to a message
		if (e.getMessage().getReferencedMessage() != null) {
			String uwu = uwuify(e.getMessage().getReferencedMessage().getContentRaw().split(" "));
			if (uwu.length() > 2000) {
				e.getChannel().sendMessage(uwu.substring(0, 2000)).queue();
				e.getChannel().sendMessage(uwu.substring(2000, uwu.length())).queue();
			} else {
				e.getChannel().sendMessage(uwu).queue();
			}
		}

		// User didn't provide text or message
		else if (args.length == 0) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Error");
			builder.setDescription("Please provide text or reply to a message!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
		}

		// Uwuify given text
		else {
			String uwu = uwuify(args);
			if (uwu.length() > 2000) {
				e.getChannel().sendMessage(uwu.substring(0, 2000)).queue();
				e.getChannel().sendMessage(uwu.substring(2000, uwu.length())).queue();
			} else {
				e.getChannel().sendMessage(uwu).queue();
			}
		}
	}

	private String uwuify(String[] raw) {
		String result = "";
		for (String s : raw) {
			String word = s.replace("you", "uwu").replace("You", "Uwu").replace("r", "w").replace("R", "W").replace("l", "w").replace("L", "W").replace("at", "awt").replace("it", "iwt")
					.replace("It", "Iwt").replace("is", "iws").replace("Is", "Iws").replace("to", "tuwu");
			result += word + " ";
		}
		return result;
	}
}