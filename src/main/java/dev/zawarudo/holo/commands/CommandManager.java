package dev.zawarudo.holo.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.commands.anime.AnimeSearchCmd;
import dev.zawarudo.holo.commands.anime.MangaSearchCmd;
import dev.zawarudo.holo.commands.general.*;
import dev.zawarudo.holo.commands.image.*;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.fun.CoinFlipCmd;
import dev.zawarudo.holo.commands.fun.Magic8BallCmd;
import dev.zawarudo.holo.commands.fun.UrbanDictionaryCmd;
import dev.zawarudo.holo.commands.fun.UwuCmd;
import dev.zawarudo.holo.commands.games.pokemon.CatchCmd;
import dev.zawarudo.holo.commands.games.pokemon.PokedexCmd;
import dev.zawarudo.holo.commands.games.pokemon.PokemonTeamCmd;
import dev.zawarudo.holo.commands.games.pokemon.SpawnCmd;
import dev.zawarudo.holo.commands.image.nsfw.BlockCmd;
import dev.zawarudo.holo.commands.image.nsfw.HoloCmd;
import dev.zawarudo.holo.commands.image.nsfw.NekoCmd;
import dev.zawarudo.holo.commands.image.nsfw.WaifuCmd;
import dev.zawarudo.holo.commands.music.cmds.ClearCmd;
import dev.zawarudo.holo.commands.music.cmds.CloneCmd;
import dev.zawarudo.holo.commands.music.cmds.JoinCmd;
import dev.zawarudo.holo.commands.music.cmds.LeaveCmd;
import dev.zawarudo.holo.commands.music.cmds.LoopCmd;
import dev.zawarudo.holo.commands.music.cmds.LyricsCmd;
import dev.zawarudo.holo.commands.music.cmds.NowPlayingCmd;
import dev.zawarudo.holo.commands.music.cmds.PlayCmd;
import dev.zawarudo.holo.commands.music.cmds.QueueCmd;
import dev.zawarudo.holo.commands.music.cmds.ShuffleCmd;
import dev.zawarudo.holo.commands.music.cmds.SkipCmd;
import dev.zawarudo.holo.commands.music.cmds.StopCmd;
import dev.zawarudo.holo.commands.owner.BlacklistCmd;
import dev.zawarudo.holo.commands.owner.CancelCmd;
import dev.zawarudo.holo.commands.owner.DeleteCmd;
import dev.zawarudo.holo.commands.owner.EchoCmd;
import dev.zawarudo.holo.commands.owner.NicknameCmd;
import dev.zawarudo.holo.commands.owner.NukeCmd;
import dev.zawarudo.holo.commands.owner.RestartCmd;
import dev.zawarudo.holo.commands.owner.ShutdownCmd;
import dev.zawarudo.holo.commands.owner.StatusCmd;
import dev.zawarudo.holo.core.Bootstrap;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates instances of all the commands and manages them
 */
public class CommandManager extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private final Map<String, AbstractCommand> commands;

    public CommandManager(EventWaiter waiter) {
        commands = new LinkedHashMap<>();

        // General Cmds
        addCommand(new BugCmd(Bootstrap.holo.getGitHubClient()));
        addCommand(new HelpCmd(this));
        addCommand(new InfoBotCmd());
        addCommand(new PermCmd());
        addCommand(new PingCmd());
        addCommand(new RoleInfoCmd());
        addCommand(new ServerEmotesCmd());
        addCommand(new ServerInfoCmd());
        addCommand(new ServerRolesCmd());
        addCommand(new SuggestionCmd(Bootstrap.holo.getGitHubClient()));
        addCommand(new WhoisCmd());

        // Anime Cmds
        addCommand(new AnimeSearchCmd(waiter));
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
        addCommand(new ShuffleCmd());
        addCommand(new SkipCmd(waiter));
        addCommand(new StopCmd());

        // Image Cmds
        addCommand(new ActionCmd());
        addCommand(new AoCStatsCmd());
        addCommand(new AvatarCmd());
        addCommand(new BannerCmd());
        addCommand(new BlockCmd());
        addCommand(new CheckNSFWCmd());
        addCommand(new DogCmd());
        addCommand(new HoloCmd());
        addCommand(new HttpCmd());
        addCommand(new InspiroCmd());
        addCommand(new NekoCmd());
        addCommand(new PaletteCmd());
        addCommand(new PixelateCmd());
        addCommand(new UpscaleCmd());
        addCommand(new WaifuCmd());
        addCommand(new XkcdCmd());

        // Game Cmds
        addCommand(new CatchCmd());
        addCommand(new PokedexCmd());
        addCommand(new PokemonTeamCmd());

        // Misc Cmds
        addCommand(new CoinFlipCmd());
        addCommand(new CountdownCmd());
        addCommand(new Magic8BallCmd());
        addCommand(new UrbanDictionaryCmd());
        addCommand(new UwuCmd());

        // Owner Cmds
        addCommand(new BlacklistCmd());
        addCommand(new CancelCmd());
        addCommand(new DeleteCmd());
        addCommand(new EchoCmd());
        addCommand(new NicknameCmd());
        addCommand(new NukeCmd());
        addCommand(new RestartCmd());
        addCommand(new ShutdownCmd());
        addCommand(new SpawnCmd());
        addCommand(new StatusCmd());

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Loaded {} commands!", commands.values().stream().distinct().count());
        }
    }

    /**
     * Registers a command. Note that commands with the {@link Deactivated} annotation are ignored.
     *
     * @param cmd The command to register.
     */
    public void addCommand(AbstractCommand cmd) {
        // Ignore deactivated commands
        if (cmd.getClass().isAnnotationPresent(Deactivated.class)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("{} is deactivated.", cmd.getClass().getSimpleName());
            }
            return;
        }

        commands.put(cmd.getName(), cmd);
        for (String alias : cmd.getAlias()) {
            commands.put(alias, cmd);
        }
    }

    /**
     * Returns the command that matches the given name.
     *
     * @param name The name of the command.
     * @return The command that matches the given name, or <code>null</code> if no command matches.
     */
    public AbstractCommand getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Check if given name is linked to a {@link AbstractCommand}.
     *
     * @param name = The name to check.
     * @return True if the name is linked to a {@link AbstractCommand}. False otherwise.
     */
    public boolean isValidName(String name) {
        return commands.containsKey(name);
    }

    /**
     * Get every {@link AbstractCommand} of a given {@link CommandCategory} as a {@link List}.
     *
     * @param category = The {@link CommandCategory} to get the commands from.
     * @return A {@link List} of {@link AbstractCommand}s.
     */
    public List<AbstractCommand> getCommands(CommandCategory category) {
        // LinkedHashSet so the list keeps the item insertion order
        Set<AbstractCommand> cmdSet = new LinkedHashSet<>();
        for (AbstractCommand cmd : this.commands.values()) {
            if (cmd.getCategory() == category) {
                cmdSet.add(cmd);
            }
        }
        return new ArrayList<>(cmdSet);
    }
}