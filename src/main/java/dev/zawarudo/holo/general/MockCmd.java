package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Command(name = "mock",
		description = "Transforms a text into a mockery",
		usage = "<text>",
		category = CommandCategory.MISC)
public class MockCmd extends AbstractCommand {

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
			String mock = mockify(e.getMessage().getReferencedMessage().getContentRaw().split(" "));
			if (mock.length() > 2000) {
				e.getChannel().sendMessage(mock.substring(0, 2000)).queue();
				e.getChannel().sendMessage(mock.substring(2000)).queue();
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
				e.getChannel().sendMessage(mock.substring(2000)).queue();
			} else {
				e.getChannel().sendMessage(mock).queue();
			}
		}
	}
	
	private String mockify(String[] raw) {
		StringBuilder result = new StringBuilder();
		boolean lower = true;
		for (String s : String.join(" ", raw).split("")) {
			if (s.equals(" ")) {
				result.append(s);
			} else if (lower) {
				result.append(s.toLowerCase(Locale.UK));
				lower = false;
			} else {
				result.append(s.toUpperCase(Locale.UK));
				lower = true;
			}
		}
		return result.toString();
	}
}