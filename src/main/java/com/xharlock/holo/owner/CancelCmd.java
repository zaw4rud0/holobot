package com.xharlock.holo.owner;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;

@Command(name = "cancel",
		description = "Cancels all the ongoing requests.",
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class CancelCmd extends AbstractCommand {

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