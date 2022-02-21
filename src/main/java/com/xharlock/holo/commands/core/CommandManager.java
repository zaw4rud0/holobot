package com.xharlock.holo.commands.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.anime.AnimeSearchCmd;
import com.xharlock.holo.anime.MangaSearchCmd;
import com.xharlock.holo.commands.cmds.BugCmd;
import com.xharlock.holo.commands.cmds.HelpCmd;
import com.xharlock.holo.commands.cmds.InfoBotCmd;
import com.xharlock.holo.commands.cmds.InspiroCmd;
import com.xharlock.holo.commands.cmds.PingCmd;
import com.xharlock.holo.commands.cmds.ServerEmotesCmd;
import com.xharlock.holo.commands.cmds.ServerInfoCmd;
import com.xharlock.holo.commands.cmds.ServerRolesCmd;
import com.xharlock.holo.commands.cmds.ServerVoiceChannelsCmd;
import com.xharlock.holo.commands.cmds.SuggestionCmd;
import com.xharlock.holo.commands.cmds.UwuCmd;
import com.xharlock.holo.commands.cmds.WhoisCmd;
import com.xharlock.holo.commands.owner.BlacklistCmd;
import com.xharlock.holo.commands.owner.CancelCmd;
import com.xharlock.holo.commands.owner.CountCmd;
import com.xharlock.holo.commands.owner.DeleteCmd;
import com.xharlock.holo.commands.owner.NicknameCmd;
import com.xharlock.holo.commands.owner.NukeCmd;
import com.xharlock.holo.commands.owner.SayCmd;
import com.xharlock.holo.commands.owner.ShutdownCmd;
import com.xharlock.holo.commands.owner.StatusCmd;
import com.xharlock.holo.games.AkinatorCmd;
import com.xharlock.holo.games.pokemon.cmds.PokedexCmd;
import com.xharlock.holo.games.pokemon.cmds.PokemonTeamCmd;
import com.xharlock.holo.games.pokemon.cmds.SpawnCmd;
import com.xharlock.holo.image.AvatarCmd;
import com.xharlock.holo.image.BannerCmd;
import com.xharlock.holo.image.BlockCmd;
import com.xharlock.holo.image.CheckNSFWCmd;
import com.xharlock.holo.image.HoloCmd;
import com.xharlock.holo.image.NekoCmd;
import com.xharlock.holo.image.UpscaleCmd;
import com.xharlock.holo.image.XkcdCmd;
import com.xharlock.holo.music.cmds.ClearCmd;
import com.xharlock.holo.music.cmds.CloneCmd;
import com.xharlock.holo.music.cmds.JoinCmd;
import com.xharlock.holo.music.cmds.LeaveCmd;
import com.xharlock.holo.music.cmds.LyricsCmd;
import com.xharlock.holo.music.cmds.NowCmd;
import com.xharlock.holo.music.cmds.PlayCmd;
import com.xharlock.holo.music.cmds.QueueCmd;
import com.xharlock.holo.music.cmds.ShuffleCmd;
import com.xharlock.holo.music.cmds.SkipCmd;
import com.xharlock.holo.music.cmds.StopCmd;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

	private Map<String, Command> commands;

	public CommandManager(EventWaiter waiter) {
		commands = new LinkedHashMap<>();

		// General Cmds
		addCommand(new BugCmd("bug"));
		addCommand(new HelpCmd("help", this));
		addCommand(new InfoBotCmd("info"));
		addCommand(new PingCmd("ping"));
		addCommand(new ServerEmotesCmd("serveremotes"));
		addCommand(new ServerInfoCmd("serverinfo"));
		addCommand(new ServerRolesCmd("serverroles"));
		addCommand(new ServerVoiceChannelsCmd("servervcs"));
		addCommand(new SuggestionCmd("suggestion"));
		addCommand(new WhoisCmd("whois"));
		
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
		addCommand(new BlockCmd("block"));
		addCommand(new CheckNSFWCmd("check"));
//		addCommand(new CollageCmd("collage"));
		addCommand(new HoloCmd("holo"));
//		addCommand(new ImageCmd("image"));
		addCommand(new NekoCmd("neko"));
		addCommand(new UpscaleCmd("upscale"));

		// Game Cmds
		addCommand(new AkinatorCmd("akinator", waiter));
		addCommand(new PokedexCmd("pokedex"));
		addCommand(new PokemonTeamCmd("pokemonteam"));
		
		// Misc Cmds
		addCommand(new InspiroCmd("inspiro"));
		addCommand(new UwuCmd("uwu"));
		addCommand(new XkcdCmd("xkcd"));

		// Owner Cmds
		addCommand(new BlacklistCmd("blacklist"));
		addCommand(new CancelCmd("cancel"));
		addCommand(new CountCmd("count"));
		addCommand(new DeleteCmd("delete"));
		addCommand(new NicknameCmd("nickname"));
		addCommand(new NukeCmd("nuke"));
//		addCommand(new PurgeCmd("purge"));
		addCommand(new SayCmd("say"));
		addCommand(new ShutdownCmd("shutdown"));
		addCommand(new SpawnCmd("spawn"));
		addCommand(new StatusCmd("status"));
	}

	public void addCommand(Command cmd) {
		commands.put(cmd.getName(), cmd);
		for (String alias : cmd.getAliases()) {
			commands.put(alias, cmd);
		}
	}

	public Command getCommand(String name) {
		return commands.get(name);
	}
	
	public Map<String, Command> getCommands() {
		return commands;
	}

	/**
	 * Get every {@link Command} of a given {@link CommandCategory} as a {@link List}
	 * 
	 * @param category = The {@link CommandCategory}
	 * @return A {@link List} of {@link Command}
	 */
	public List<Command> getCommands(CommandCategory category) {		
		// LinkedHashSet so the list keeps the item insertion order
		Set<Command> commands = new LinkedHashSet<>();
		
		for (Command cmd : this.commands.values()) {
			if (cmd.getCommandCategory() == category) {
				commands.add(cmd);
			}
		}
		return new ArrayList<>(commands);
	}

	/**
	 * Check if given name is linked to a {@link Command}
	 */
	public boolean isValidName(String name) {
		return commands.containsKey(name);
	}

	/**
	 * Check if given name is among the aliases of a {@link Command}
	 */
	public boolean isAlias(Command cmd, String name) {
		return cmd.getAliases().contains(name);
	}
}
