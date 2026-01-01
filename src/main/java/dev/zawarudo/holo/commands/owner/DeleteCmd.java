package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "delete",
		description = "Deletes a message of your choice. This works by either passing the message id or replying to a message.",
		usage = "[msg id]",
		alias = {"d"},
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class DeleteCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		// Delete message user is replying to
		if (event.getMessage().getReferencedMessage() != null) {
			event.getMessage().getReferencedMessage().delete().queue();
			return;
		}

		EmbedBuilder builder = new EmbedBuilder();

		// No argument was given
		if (args.length != 1) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please only provide the id of the message you want to delete!");
			sendToOwner(builder);
			return;
		}

		long id;

		try {
			id = Long.parseLong(args[0]);
		} catch (NumberFormatException ex) {
			builder.setTitle("Error");
			builder.setDescription("Please provide the id of the message you want to delete!");
			sendToOwner(builder);
			return;
		}

		event.getChannel().retrieveMessageById(id).complete().delete().queue(v -> {}, err -> {});
	}
}