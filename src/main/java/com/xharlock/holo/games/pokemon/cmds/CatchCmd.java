package com.xharlock.holo.games.pokemon.cmds;

import com.xharlock.holo.commands.core.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CatchCmd extends Command {

	public CatchCmd(String name) {
		super(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getChannel().sendMessage("This feature is not implemented yet!").queue();
	}
}
