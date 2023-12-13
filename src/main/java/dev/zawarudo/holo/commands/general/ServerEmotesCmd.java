package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO:
//  * Rework command
//  * Error when sending embed, embed has more than 6000 characters!

@Command(name = "serveremotes",
		description = "Shows all emotes on the server",
		category = CommandCategory.GENERAL)
public class ServerEmotesCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Emotes of " + e.getGuild().getName());
		List<RichCustomEmoji> emotes = e.getGuild().getEmojis().stream().filter(em -> em.canInteract(e.getGuild().getSelfMember())).toList();
		
		if (emotes.isEmpty()) {
			builder.setDescription("This server doesn't have any emotes.");
			sendEmbed(e, builder, true, 2, TimeUnit.MINUTES);
			return;
		}

		// Separate normal emotes from animated ones
		List<String> normal = new ArrayList<>();
		List<String> animated = new ArrayList<>();
		emotes.stream().filter(RichCustomEmoji::isAnimated).map(RichCustomEmoji::getAsMention).map(String::toLowerCase).forEach(animated::add);
		emotes.stream().filter(em -> !em.isAnimated()).map(RichCustomEmoji::getAsMention).map(String::toLowerCase).forEach(animated::add);

		int charCount = 0;

		// TODO: Make this less ugly -> Methods to build each role string

		if (!normal.isEmpty()) {
			Collections.sort(normal);
			StringBuilder normalString = new StringBuilder();

			for (String s : normal) {
				// A single embed has a char limit of 6000
				if (charCount + s.length() > 6000) {
					sendEmbed(e, builder, true, 2, TimeUnit.MINUTES);
					builder.clearFields();
					charCount = 0;
				}
				// A field has a char limit of 1024
				if (normalString.length() + s.length() > 1024) {
					builder.addField("", normalString.toString(), false);
					normalString = new StringBuilder(s);
				} else {
					normalString.append(s);
				}
				charCount += s.length();
			}
			builder.addField("", normalString.toString(), false);
		}

		if (!animated.isEmpty()) {
			Collections.sort(animated);
			StringBuilder animatedString = new StringBuilder();

			for (String s : animated) {
				// A single embed has a char limit of 6000
				if (charCount + s.length() > 6000) {
					sendEmbed(e, builder, true, 2, TimeUnit.MINUTES);
					builder.clearFields();
					charCount = 0;
				}
				// A field has a char limit of 1024
				if (animatedString.length() + s.length() > 1024) {
					builder.addField("", animatedString.toString(), false);
					animatedString = new StringBuilder(s);
				} else {
					animatedString.append(s);
				}
				charCount += s.length();
			}
			builder.addField("", animatedString.toString(), false);
		}
		sendEmbed(e, builder, true, 2, TimeUnit.MINUTES);
	}
}