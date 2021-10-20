package com.xharlock.holo.commands.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.anime.*;
import com.xharlock.holo.commands.cmds.*;
import com.xharlock.holo.commands.owner.*;
import com.xharlock.holo.games.AkinatorCmd;
import com.xharlock.holo.games.pokemon.*;
import com.xharlock.holo.image.*;
import com.xharlock.holo.music.cmds.*;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

	private LinkedHashMap<String, Command> commands;

	public CommandManager(EventWaiter waiter) {
		this.commands = new LinkedHashMap<>();

		// General Cmds
		addCommand(new BugCmd("bug"));
		addCommand(new ExamCmd("bp"));
		addCommand(new HelpCmd("help", this));
		addCommand(new InfoBotCmd("info"));
		addCommand(new PingCmd("ping"));
		addCommand(new ServerEmotesCmd("serveremotes"));
		addCommand(new ServerInfoCmd("serverinfo"));
		addCommand(new ServerRolesCmd("serverroles"));
		addCommand(new SuggestionCmd("suggestion"));
		addCommand(new WhoisCmd("whois"));
		
		// Experimental Commands
//		addCommand(new ReadCmd("read"));
//		addCommand(new PokemonCmd("pokemon"));
//		addCommand(new ButtonCmd("button"));
//		addCommand(new TestCmd("test"));
		
		// Anime Cmds
		addCommand(new AnimeSearchCmd("animesearch", waiter));
		addCommand(new MangaSearchCmd("mangasearch", waiter));

		// Music Cmds
		addCommand(new ClearCmd("clear", waiter));
		addCommand(new CloneCmd("clone"));
		addCommand(new JoinCmd("join"));
		addCommand(new LeaveCmd("leave"));
//		addCommand(new LoopCmd("loop"));
		addCommand(new LyricsCmd("lyrics"));
		addCommand(new NowCmd("now"));
//		addCommand(new PauseCmd("pause"));
		addCommand(new PlayCmd("play"));
		addCommand(new QueueCmd("queue"));
//		addCommand(new RemoveCmd("remove"));
		addCommand(new ShuffleCmd("shuffle"));
		addCommand(new SkipCmd("skip", waiter));
		addCommand(new StopCmd("stop"));

		// Image Cmds
		addCommand(new AvatarCmd("avatar"));
		addCommand(new BannerCmd("banner"));
//		addCommand(new BlockCmd("block"));
		addCommand(new CheckNSFWCmd("check"));
//		addCommand(new CollageCmd("collage"));
//		addCommand(new HoloCmd("holo"));
//		addCommand(new ImageCmd("image"));
//		addCommand(new NekoCmd("neko"));
		addCommand(new UpscaleCmd("upscale"));

		// Game Cmds
		addCommand(new AkinatorCmd("akinator", waiter));
		addCommand(new PokedexCmd("pokedex"));
		addCommand(new PokemonTeamCmd("pokemonteam"));
		addCommand(new RandomPokemonCmd("randompokemon"));
		
		// Misc Cmds
		addCommand(new InspiroCmd("inspiro"));
		addCommand(new UwuCmd("uwu"));
		addCommand(new xkcdCmd("xkcd"));

		// Owner Cmds
		addCommand(new BlacklistCmd("blacklist"));
		addCommand(new CancelCmd("cancel"));
		addCommand(new CountCmd("count"));
		addCommand(new DeleteCmd("delete"));
		addCommand(new NicknameCmd("nickname"));
		addCommand(new NukeCmd("nuke"));
		addCommand(new PurgeCmd("purge"));
		addCommand(new SayCmd("say"));
		addCommand(new ShutdownCmd("shutdown"));
		addCommand(new StatusCmd("status"));
	}

	public void addCommand(Command cmd) {
		this.commands.put(cmd.getName(), cmd);
		for (String alias : cmd.getAliases())
			this.commands.put(alias, cmd);
	}

	public Command getCommand(String name) {
		return this.commands.get(name);
	}
	
	public LinkedHashMap<String, Command> getCommands() {
		return this.commands;
	}

	/**
	 * Method to get every {@link Command} of a given {@link CommandCategory} as a {@link List}
	 * 
	 * @param category = The {@link CommandCategory}
	 * @return A {@link List} of {@link Command}
	 */
	public List<Command> getCommands(CommandCategory category) {		
		// LinkedHashSet so the List keeps the item insertion order
		LinkedHashSet<Command> commands = new LinkedHashSet<>();
		
		for (Command cmd : this.commands.values()) {
			if (cmd.getCommandCategory() == category) {
				commands.add(cmd);
			}
		}
		return new ArrayList<>(commands);
	}

	/**
	 * Method to check if a given name is linked to a {@link Command}
	 */
	public boolean isValidName(String name) {
		return this.commands.containsKey(name);
	}

	/**
	 * Method to check if a given name is among the aliases of a {@link Command}
	 */
	public boolean isAlias(Command cmd, String name) {
		return cmd.getAliases().contains(name);
	}
}
