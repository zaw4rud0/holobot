package com.xharlock.holo.commands.owner;

import java.util.List;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteCmd extends Command {

	public DeleteCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to delete a message using their id. Replying to a message also works.");
		setUsage(name + " [msg id]");
		setAliases(List.of("d"));
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();

		EmbedBuilder builder = new EmbedBuilder();

		// Delete message user is replying to
		if (e.getMessage().getReferencedMessage() != null) {
			e.getMessage().getReferencedMessage().delete().queue();
			return;
		}
		
		// No argument was given
		if (args.length != 1) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please only provide the id of the message you want to delete!");
			sendToOwner(e, builder);
			return;
		}

		long id = 0;

		// Couldn't parse id
		try {
			id = Long.parseLong(args[0]);
		} catch (NumberFormatException ex) {
			builder.setTitle("Error");
			builder.setDescription("Please provide the id of the message you want to delete!");
			sendToOwner(e, builder);
			return;
		}

		// Couldn't find message		
		e.getChannel().retrieveMessageById(id).complete().delete().queue(v -> {}, err -> {});
	}
}
