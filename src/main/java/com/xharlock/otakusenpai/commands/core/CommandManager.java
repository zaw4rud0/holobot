package com.xharlock.otakusenpai.commands.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.cmds.*;
import com.xharlock.otakusenpai.commands.owner.*;
import com.xharlock.otakusenpai.games.AkinatorCmd;
import com.xharlock.otakusenpai.games.pokemon.*;
import com.xharlock.otakusenpai.image.*;
import com.xharlock.otakusenpai.music.cmds.*;
import com.xharlock.otakusenpai.place.*;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

	private LinkedHashMap<String, Command> commands;

	public CommandManager(EventWaiter waiter) {
		this.commands = new LinkedHashMap<>();

		// General Cmds
		addCommand(new BugCmd("bug"));
		addCommand(new HelpCmd("help", this));
		addCommand(new InfoBotCmd("info"));
		addCommand(new PingCmd("ping"));
//		addCommand(new ServerInfoCmd("serverinfo"));
		addCommand(new SuggestionCmd("suggestion"));
		addCommand(new WhoisCmd("whois"));

		// Anime Cmds
//		addCommand(new AnimeCmd("anime"));
//		addCommand(new MangaCmd("manga"));
//		addCommand(new AnimeSearchCmd("animesearch", waiter));
//		addCommand(new MangaSearchCmd("mangasearch", waiter));

		// Music Cmds
		addCommand(new ClearCmd("clear", waiter));
//		addCommand(new CloneCmd("clone"));
		addCommand(new JoinCmd("join"));
		addCommand(new LeaveCmd("leave"));
//		addCommand(new LoopCmd("loop"));
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
		addCommand(new CheckNSFWCmd("check"));
//		addCommand(new CollageCmd("collage"));
		addCommand(new HoloCmd("holo"));
		addCommand(new ImageCmd("image"));
		addCommand(new NekoCmd("neko"));
		addCommand(new UpscaleCmd("upscale"));

		// Game Cmds
		addCommand(new AkinatorCmd("akinator", waiter));
		addCommand(new PokedexCmd("pokedex"));
		addCommand(new PokemonTeamCmd("pokemonteam"));
		addCommand(new RandomPokemonCmd("randompokemon"));		

		// Place Cmds
		addCommand(new ConvertCmd("convert"));
		addCommand(new DrawTxtCmd("drawtxt"));
		addCommand(new BullyCmd("bully"));
		
		// Misc Cmds
		addCommand(new InspiroCmd("inspiro"));
		addCommand(new xkcdCmd("xkcd"));

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
		LinkedHashSet<Command> commands = new LinkedHashSet<>();
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
