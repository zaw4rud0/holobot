package com.xharlock.holo.commands.owner;

import java.time.Instant;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CancelCmd extends Command {

	public CancelCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to cancel all ongoing requests");
		setUsage(name);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		e.getJDA().cancelRequests();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Success");
		builder.setDescription("Cancelled all requests");
		builder.setTimestamp(Instant.now());
		sendToOwner(e, builder);
	}
}