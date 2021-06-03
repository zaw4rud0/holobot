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

public class ServerEmotes extends Command {

	public ServerEmotes(String name) {
		super(name);
		setDescription("Use this command to view all emotes of the server");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		List<Emote> emotes = e.getGuild().getEmotes();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Emotes of " + e.getGuild().getName());

		// 0 optimization :kekhands:
		
		if (emotes.size() == 0) {
			builder.setDescription("This server doesn't have any emotes");
		} else {
			List<String> normal = new ArrayList<>();
			List<String> animated = new ArrayList<>();

			for (Emote em : emotes) {
				if (em.isAnimated())
					animated.add(em.getAsMention().toLowerCase());
				else
					normal.add(em.getAsMention().toLowerCase());
			}

			Collections.sort(normal);
			Collections.sort(animated);

			String normal_string = "";
			String animated_string = "";

			for (String s : normal) {
				if (normal_string.length() + s.length() > 1024) {
					builder.addField("", normal_string, false);
					normal_string = s;
				} else {
					normal_string += s;
				}
			}

			builder.addField("", normal_string, false);

			for (String s : animated) {
				if (animated_string.length() + s.length() > 1024) {
					builder.addField("", animated_string, false);
					animated_string = s;
				} else {
					animated_string += s;
				}
			}
			builder.addField("", animated_string, false);
		}
		sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
	}
}
