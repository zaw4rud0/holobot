package com.xharlock.otakusenpai.commands.owner;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import java.util.List;

import com.xharlock.otakusenpai.commands.core.Command;

public class DeleteCmd extends Command {

	public DeleteCmd(String name) {
		super(name);
		setDescription("Use this command to delete a message");
		setUsage(name + " [msg id]");
		setAliases(List.of());
		setIsOwnerCommand(true);
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (getArgs().length != 1)
			return;
		e.getChannel().retrieveMessageById(getArgs()[0]).complete().delete().queue(v -> {}, err -> {});
	}
}
