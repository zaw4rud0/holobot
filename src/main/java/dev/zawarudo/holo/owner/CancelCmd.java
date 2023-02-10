package dev.zawarudo.holo.owner;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Command to cancel all requests to JDA.
 */
@Command(name = "cancel",
		description = "Cancels all the ongoing requests.",
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class CancelCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);
		e.getJDA().cancelRequests();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Success");
		builder.setDescription("Cancelled all requests");
		builder.setTimestamp(Instant.now());
		sendToOwner(e, builder);
	}
}