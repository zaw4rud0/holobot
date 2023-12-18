package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
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
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);
		event.getJDA().cancelRequests();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Success");
		builder.setDescription("Cancelled all requests");
		builder.setTimestamp(Instant.now());
		sendToOwner(builder);
	}
}