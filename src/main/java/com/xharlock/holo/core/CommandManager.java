package com.xharlock.holo.core;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.anime.AnimeSearchCmd;
import com.xharlock.holo.anime.BestAnimesCmd;
import com.xharlock.holo.anime.CharacterSearchCmd;
import com.xharlock.holo.anime.MangaSearchCmd;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.config.ConfigCmd;
import com.xharlock.holo.development.AoCStatsCmd;
import com.xharlock.holo.development.CollageCmd;
import com.xharlock.holo.games.akinator.AkinatorCmd;
import com.xharlock.holo.games.akinator.AkinatorCmdOld;
import com.xharlock.holo.games.pokemon.CatchCmd;
import com.xharlock.holo.games.pokemon.PokedexCmd;
import com.xharlock.holo.games.pokemon.PokemonTeamCmd;
import com.xharlock.holo.games.pokemon.SpawnCmd;
import com.xharlock.holo.general.*;
import com.xharlock.holo.image.*;
import com.xharlock.holo.image.meme.GenerateCmd;
import com.xharlock.holo.music.cmds.*;
import com.xharlock.holo.owner.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Creates instances of all the commands and manages them
 */
public class CommandManager extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

	private final Map<String, AbstractCommand> commands;

	public CommandManager(EventWaiter waiter) {
		commands = new LinkedHashMap<>();

		// General Cmds
		addCommand(new BugCmd());
		addCommand(new ConfigCmd(Bootstrap.holo.getGuildConfigManager()));
		addCommand(new HelpCmd(this));
		addCommand(new InfoBotCmd());
		addCommand(new PermCmd());
		addCommand(new PingCmd());
		addCommand(new RoleInfoCmd());
		addCommand(new ServerEmotesCmd());
		addCommand(new ServerInfoCmd());
		addCommand(new ServerRolesCmd());
		addCommand(new SuggestionCmd());
		addCommand(new WhoisCmd());
		
		// Anime Cmds
		addCommand(new AnimeSearchCmd(waiter));
		addCommand(new BestAnimesCmd());
		addCommand(new CharacterSearchCmd(waiter));
		addCommand(new MangaSearchCmd(waiter));

		// Music Cmds
		addCommand(new ClearCmd(waiter));
		addCommand(new CloneCmd());
		addCommand(new JoinCmd());
		addCommand(new LeaveCmd());
		addCommand(new LoopCmd());
		addCommand(new LyricsCmd());
		addCommand(new NowPlayingCmd());
		addCommand(new PlayCmd());
		addCommand(new QueueCmd());
		addCommand(new RemoveCmd());
		addCommand(new ShuffleCmd());
		addCommand(new SkipCmd(waiter));
		addCommand(new StopCmd());

		// Image Cmds
		addCommand(new ActionCmd());
		addCommand(new AoCStatsCmd());
		addCommand(new AvatarCmd());
		addCommand(new BannerCmd());
		addCommand(new BlockCmd());
		addCommand(new BlurCmd());
		addCommand(new CatCmd());
		addCommand(new CheckNSFWCmd());
		addCommand(new CollageCmd());
		addCommand(new DanbooruCmd());
		addCommand(new DogCmd());
		addCommand(new DreamCmd());
		addCommand(new GelbooruCmd());
		addCommand(new GenerateCmd());
		addCommand(new HoloCmd());
		addCommand(new InspiroCmd());
		addCommand(new WaifuCmd());
		addCommand(new NekoCmd());
		addCommand(new PixelateCmd());
		addCommand(new UpscaleCmd());
		addCommand(new XkcdCmd());

		// Game Cmds
		addCommand(new AkinatorCmd());
		addCommand(new AkinatorCmdOld(waiter));
		addCommand(new CatchCmd());
		addCommand(new PokedexCmd());
		addCommand(new PokemonTeamCmd());
		
		// Misc Cmds
		addCommand(new Magic8BallCmd());
		addCommand(new CoinFlipCmd());
		addCommand(new MockCmd());
		addCommand(new UwuCmd());

		// Owner Cmds
		addCommand(new BlacklistCmd());
		addCommand(new CancelCmd());
		addCommand(new DeleteCmd());
		addCommand(new EchoCmd());
		addCommand(new NicknameCmd());
		addCommand(new NukeCmd());
		addCommand(new PurgeCmd());
		addCommand(new RestartCmd());
		addCommand(new ShutdownCmd());
		addCommand(new SpawnCmd());
		addCommand(new StatusCmd());
	}

	/**
	 * Registers a command. Note that commands with the {@link Deactivated} annotation are ignored.
	 */
	public void addCommand(AbstractCommand cmd) {
		// Ignore deactivated commands
		if (cmd.getClass().isAnnotationPresent(Deactivated.class)) {
			if (logger.isInfoEnabled()) {
				logger.info(cmd.getClass().getSimpleName() + " is deactivated.");
			}
			return;
		}
		commands.put(cmd.getName(), cmd);
		for (String alias : cmd.getAlias()) {
			commands.put(alias, cmd);
		}
	}

	/**
	 * Returns the command that matches the given name
	 */
	public AbstractCommand getCommand(String name) {
		return commands.get(name);
	}

	/**
	 * Get every {@link AbstractCommand} of a given {@link CommandCategory} as a {@link List}
	 * 
	 * @param category = The {@link CommandCategory}
	 * @return A {@link List} of {@link AbstractCommand}
	 */
	public List<AbstractCommand> getCommands(CommandCategory category) {
		// LinkedHashSet so the list keeps the item insertion order
		Set<AbstractCommand> commands = new LinkedHashSet<>();
		
		for (AbstractCommand cmd : this.commands.values()) {
			if (cmd.getCategory() == category) {
				commands.add(cmd);
			}
		}
		return new ArrayList<>(commands);
	}

	/**
	 * Check if given name is linked to a {@link AbstractCommand}
	 */
	public boolean isValidName(String name) {
		return commands.containsKey(name);
	}
}