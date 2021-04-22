package com.xharlock.otakusenpai.commands.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.cmds.BugCmd;
import com.xharlock.otakusenpai.commands.cmds.HelpCmd;
import com.xharlock.otakusenpai.commands.cmds.InfoBotCmd;
import com.xharlock.otakusenpai.commands.cmds.InspiroCmd;
import com.xharlock.otakusenpai.commands.cmds.PingCmd;
import com.xharlock.otakusenpai.commands.cmds.SuggestionCmd;
import com.xharlock.otakusenpai.commands.cmds.WhoisCmd;
import com.xharlock.otakusenpai.commands.owner.CancelCmd;
import com.xharlock.otakusenpai.commands.owner.DeleteCmd;
import com.xharlock.otakusenpai.commands.owner.NicknameCmd;
import com.xharlock.otakusenpai.commands.owner.SayCmd;
import com.xharlock.otakusenpai.commands.owner.ShutdownCmd;
import com.xharlock.otakusenpai.commands.owner.StatusCmd;
import com.xharlock.otakusenpai.games.AkinatorCmd;
import com.xharlock.otakusenpai.games.pokemon.PokedexCmd;
import com.xharlock.otakusenpai.games.pokemon.PokemonTeamCmd;
import com.xharlock.otakusenpai.games.pokemon.RandomPokemonCmd;
import com.xharlock.otakusenpai.image.AvatarCmd;
import com.xharlock.otakusenpai.image.BannerCmd;
import com.xharlock.otakusenpai.image.HoloCmd;
import com.xharlock.otakusenpai.image.ImageCmd;
import com.xharlock.otakusenpai.image.NekoCmd;
import com.xharlock.otakusenpai.image.UpscaleCmd;
import com.xharlock.otakusenpai.music.cmds.ClearCmd;
import com.xharlock.otakusenpai.music.cmds.JoinCmd;
import com.xharlock.otakusenpai.music.cmds.LeaveCmd;
import com.xharlock.otakusenpai.music.cmds.NowCmd;
import com.xharlock.otakusenpai.music.cmds.PlayCmd;
import com.xharlock.otakusenpai.music.cmds.QueueCmd;
import com.xharlock.otakusenpai.music.cmds.ShuffleCmd;
import com.xharlock.otakusenpai.place.BullyCmd;
import com.xharlock.otakusenpai.place.ConvertCmd;
import com.xharlock.otakusenpai.place.DrawTxtCmd;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

	private LinkedHashMap<String, Command> commands;

	public CommandManager(EventWaiter waiter) {
		this.commands = new LinkedHashMap<>();

		// General Cmds
		addCommand(new BugCmd("bug"));
		addCommand(new HelpCmd("help", this));
		addCommand(new InfoBotCmd("info"));
		addCommand(new SuggestionCmd("suggestion"));
		addCommand(new PingCmd("ping"));
		addCommand(new WhoisCmd("whois"));

		// Anime Cmds
//		addCommand(new AnimeCmd("anime"));
//		addCommand(new MangaCmd("manga"));
//		addCommand(new AnimeSearchCmd("animesearch", waiter));
//		addCommand(new MangaSearchCmd("mangasearch", waiter));

		// Music Cmds
		addCommand(new JoinCmd("join"));
		addCommand(new LeaveCmd("leave"));
		addCommand(new PlayCmd("play"));
		addCommand(new NowCmd("now"));
		addCommand(new QueueCmd("queue"));
		addCommand(new ClearCmd("clear"));
		addCommand(new ShuffleCmd("shuffle"));
		
		// Image Cmds
		addCommand(new AvatarCmd("avatar"));
		addCommand(new BannerCmd("banner"));
		addCommand(new HoloCmd("holo"));
		addCommand(new ImageCmd("image"));
		addCommand(new NekoCmd("neko"));
		addCommand(new UpscaleCmd("upscale"));

		// Game Cmds
		addCommand(new AkinatorCmd("akinator", waiter));
		addCommand(new PokedexCmd("pokedex"));
		addCommand(new RandomPokemonCmd("randompokemon"));
		addCommand(new PokemonTeamCmd("pokemonteam"));

		// Place Cmds
		addCommand(new ConvertCmd("convert"));
		addCommand(new DrawTxtCmd("drawtxt"));
		addCommand(new BullyCmd("bully"));
		
		// Misc Cmds
		addCommand(new InspiroCmd("inspiro"));

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
