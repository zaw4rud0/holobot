package com.xharlock.holo.commands.owner;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CountCmd extends Command {

	public CountCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to send a certain amount of numbered messages. Perfect to test purging commands or to learn how to count.");
		setUsage(name + " <amount>");
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		int amount = 0;
		
		try {
			amount = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {return;}
		
		for (int i = 1; i <= amount; i++) {
			e.getChannel().sendMessage("This is message #" + i).queue();
		}
	}
}
