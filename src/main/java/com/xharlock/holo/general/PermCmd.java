package com.xharlock.holo.general;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "perm",
		description = "Shows my permissions in this channel",
		category = CommandCategory.GENERAL)
public class PermCmd extends AbstractCommand {

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