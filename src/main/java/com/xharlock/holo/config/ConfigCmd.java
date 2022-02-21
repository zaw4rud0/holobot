package com.xharlock.holo.config;

import com.xharlock.holo.commands.core.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command that lets the bot owner, the guild owner and the guild admins to
 * change the configurations of the bot for the guild in question.
 */
public class ConfigCmd extends Command {

	public ConfigCmd(String name) {
		super(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
	}
}