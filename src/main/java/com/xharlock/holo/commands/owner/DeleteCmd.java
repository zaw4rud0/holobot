package com.xharlock.holo.commands.owner;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteCmd extends Command {

	public DeleteCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to delete a message");
		setUsage(name + " [msg id]");
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		if (args.length != 1)
			return;
		e.getChannel().retrieveMessageById(args[0]).complete().delete().queue(v -> {}, err -> {});
	}
}
