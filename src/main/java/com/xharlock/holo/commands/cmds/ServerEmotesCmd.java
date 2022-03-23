package com.xharlock.holo.commands.cmds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ServerEmotesCmd extends Command {

	public ServerEmotesCmd(String name) {
		super(name);
		setDescription("Use this command to view all emotes of the server.");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Emotes of " + e.getGuild().getName());

		List<Emote> emotes = e.getGuild().getEmotes().stream().filter(em -> em.canInteract(e.getGuild().getSelfMember())).toList();
		
		if (emotes.isEmpty()) {
			builder.setDescription("This server doesn't have any emotes.");
			sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
			return;
		}

		// Separate normal emotes from animated onese
		List<String> normal = new ArrayList<>();
		List<String> animated = new ArrayList<>();
		emotes.stream().filter(em -> em.isAnimated()).map(Emote::getAsMention).map(String::toLowerCase).forEach(animated::add);
		emotes.stream().filter(em -> !em.isAnimated()).map(Emote::getAsMention).map(String::toLowerCase).forEach(animated::add);

		int charCount = 0;

		if (!normal.isEmpty()) {
			Collections.sort(normal);
			String normalString = "";

			for (String s : normal) {
				// A single embed has a char limit of 6000
				if (charCount + s.length() > 6000) {
					sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
					builder.clearFields();
					charCount = 0;
				}
				// A field has a char limit of 1024
				if (normalString.length() + s.length() > 1024) {
					builder.addField("", normalString, false);
					normalString = s;
				} else {
					normalString += s;
				}
				charCount += s.length();
			}
			builder.addField("", normalString, false);
		}

		if (!animated.isEmpty()) {
			Collections.sort(animated);
			String animatedString = "";

			for (String s : animated) {
				// A single embed has a char limit of 6000
				if (charCount + s.length() > 6000) {
					sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
					builder.clearFields();
					charCount = 0;
				}
				// A field has a char limit of 1024
				if (animatedString.length() + s.length() > 1024) {
					builder.addField("", animatedString, false);
					animatedString = s;
				} else {
					animatedString += s;
				}
				charCount += s.length();
			}
			builder.addField("", animatedString, false);
		}
		sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
	}
}