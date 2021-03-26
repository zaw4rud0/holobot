package com.xharlock.otakusenpai.commands.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.cmds.InfoBotCmd;
import com.xharlock.otakusenpai.commands.cmds.PingCmd;
import com.xharlock.otakusenpai.games.AkinatorCmd;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {
	
	private LinkedHashMap<String, Command> commands;
	
	public CommandManager(EventWaiter waiter) {
		this.commands = new LinkedHashMap<>();
		
		// GENERAL CMDS
		addCommand(new PingCmd("ping"));
		addCommand(new InfoBotCmd("info"));
		
		// ANIME CMDS
		
		// MUSIC CMDS
		
		// IMAGE CMDS
		
		// MISC CMDS
		addCommand(new AkinatorCmd("akinator", waiter));
		
	}
	
	public void addCommand(Command cmd) {
		this.commands.put(cmd.getName(), cmd);
		for (String alias : cmd.getAliases())
			this.commands.put(alias, cmd);
	}
	
	public LinkedHashMap<String, Command> getCommands() {
		return this.commands;
	}
	
	public List<Command> getCommands(CommandCategory category){
		// Storing commands in a HashSet so there won't be any duplicates
		HashSet<Command> commands = new HashSet<>();
		for (Command cmd : this.commands.values())
			if (cmd.getCommandCategory() == category)
				commands.add(cmd);
		return new ArrayList<>(commands);
	}

	public Command getCommand(String name) {
		return this.commands.get(name);
	}
	
	public boolean isValidName(String name) {
		return this.commands.containsKey(name);
	}

	public boolean isAlias(Command cmd, String name) {
		return cmd.getAliases().contains(name);
	}
}
