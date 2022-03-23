package com.xharlock.holo.commands.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermCmd extends Command {

	public PermCmd(String name) {
		super(name);
		setDescription("Use this command to see the perms of this bot within this guild");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();		
		builder.setTitle("My Perms");
		String s = "";
		for (Permission perm : e.getGuild().getSelfMember().getPermissions()) {
			s += perm.getName() + "\n";
		}
		builder.setDescription(s);
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}