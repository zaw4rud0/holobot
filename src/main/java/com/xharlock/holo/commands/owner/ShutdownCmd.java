package com.xharlock.holo.commands.owner;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownCmd extends Command {

	public ShutdownCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to shut down the bot");
		setUsage(name);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);		
		Runtime.getRuntime().exit(0);
	}
}