package com.xharlock.otakusenpai.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ServerRoles extends Command {

	public ServerRoles(String name) {
		super(name);
		setDescription("Use this command to display all roles of this server");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		List<Role> roles = e.getGuild().getRoles();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Roles of " + e.getGuild().getName());

		if (roles.size() == 0) {
			builder.setDescription("This server doesn't have any roles");
		} else {
			String s = "";
			int counter = 0;
			for (Role r : roles) {
				String role = r.getAsMention() + "\n(" + r.getId() + ")";
				if (s.length() + role.length() > 1024) {
					builder.addField("" + counter++, s, true);
					s = role + "\n";
				} else {
					s += role + "\n";
				}
			}
			builder.addField("" + counter++, s, true);
		}
		sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
	}
}
