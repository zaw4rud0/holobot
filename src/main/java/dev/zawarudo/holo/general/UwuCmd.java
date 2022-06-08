package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "uwu",
		description = "Uwuifies a text of your choice. You can also reply to a message to uwuify it.",
		usage = "<text>",
		category = CommandCategory.MISC)
public class UwuCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		// Check for pings
		if (hasPings(e.getMessage())) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Error");
			builder.setDescription("No pings!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
			return;
		}

		// No emotes
		if (!e.getMessage().getMentions().getEmotes().isEmpty()) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Error");
			builder.setDescription("No emotes!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
			return;
		}

		// User replied to a message
		if (e.getMessage().getReferencedMessage() != null) {
			String uwu = uwuify(e.getMessage().getReferencedMessage().getContentRaw().split(" "));
			if (uwu.length() > 2000) {
				e.getChannel().sendMessage(uwu.substring(0, 2000)).queue();
				e.getChannel().sendMessage(uwu.substring(2000)).queue();
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
				e.getChannel().sendMessage(uwu.substring(2000)).queue();
			} else {
				e.getChannel().sendMessage(uwu).queue();
			}
		}
	}

	private String uwuify(String[] raw) {
		StringBuilder result = new StringBuilder();
		for (String s : raw) {
			String word = s.replace("you", "uwu").replace("You", "Uwu").replace("r", "w").replace("R", "W").replace("l", "w").replace("L", "W").replace("at", "awt").replace("it", "iwt")
					.replace("It", "Iwt").replace("is", "iws").replace("Is", "Iws").replace("to", "tuwu");
			result.append(word).append(" ");
		}
		return result.toString();
	}
}