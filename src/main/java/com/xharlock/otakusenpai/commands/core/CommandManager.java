package com.xharlock.otakusenpai.commands.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.cmds.BugCmd;
import com.xharlock.otakusenpai.commands.cmds.InfoBotCmd;
import com.xharlock.otakusenpai.commands.cmds.PingCmd;
import com.xharlock.otakusenpai.commands.cmds.SuggestionCmd;
import com.xharlock.otakusenpai.commands.owner.CancelCmd;
import com.xharlock.otakusenpai.commands.owner.DeleteCmd;
import com.xharlock.otakusenpai.commands.owner.NicknameCmd;
import com.xharlock.otakusenpai.commands.owner.SayCmd;
import com.xharlock.otakusenpai.commands.owner.ShutdownCmd;
import com.xharlock.otakusenpai.commands.owner.StatusCmd;
import com.xharlock.otakusenpai.games.AkinatorCmd;
import com.xharlock.otakusenpai.games.pokemon.PokedexCmd;
import com.xharlock.otakusenpai.games.pokemon.PokemonTeamCmd;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

	private LinkedHashMap<String, Command> commands;

	public CommandManager(EventWaiter waiter) {
		this.commands = new LinkedHashMap<>();

		// General Cmds
		addCommand(new BugCmd("bug"));
		addCommand(new SuggestionCmd("suggestion"));
		addCommand(new PingCmd("ping"));
		addCommand(new InfoBotCmd("info"));

		// ANIME CMDS

		// MUSIC CMDS

		// IMAGE CMDS

		// Game Cmds
		addCommand(new AkinatorCmd("akinator", waiter));
		addCommand(new PokedexCmd("pokedex"));
		addCommand(new PokemonTeamCmd("pokemonteam"));

		// Misc Cmds

		// Owner Cmds
		addCommand(new CancelCmd("cancel"));
		addCommand(new DeleteCmd("delete"));
		addCommand(new NicknameCmd("nickname"));
		addCommand(new SayCmd("say"));
		addCommand(new ShutdownCmd("shutdown"));
		addCommand(new StatusCmd("status"));
	}

	public void addCommand(Command cmd) {
		this.commands.put(cmd.getName(), cmd);
		for (String alias : cmd.getAliases())
			this.commands.put(alias, cmd);
	}

	public LinkedHashMap<String, Command> getCommands() {
		return this.commands;
	}

	public List<Command> getCommands(CommandCategory category) {
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
