package com.xharlock.otakusenpai.commands.owner;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import com.xharlock.otakusenpai.commands.core.Command;

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
		if (args.length != 1)
			return;
		e.getChannel().retrieveMessageById(args[0]).complete().delete().queue(v -> {}, err -> {});
	}
}
