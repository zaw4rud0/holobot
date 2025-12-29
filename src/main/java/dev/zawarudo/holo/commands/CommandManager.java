package dev.zawarudo.holo.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.commands.anime.AnimeSearchCmd;
import dev.zawarudo.holo.commands.anime.MangaSearchCmd;
import dev.zawarudo.holo.commands.fun.*;
import dev.zawarudo.holo.commands.general.*;
import dev.zawarudo.holo.commands.image.*;
import dev.zawarudo.holo.commands.owner.*;
import dev.zawarudo.holo.core.GuildConfigManager;
import dev.zawarudo.holo.core.PermissionManager;
import dev.zawarudo.holo.database.dao.CountdownDao;
import dev.zawarudo.holo.database.dao.XkcdDao;
import dev.zawarudo.holo.modules.GitHubClient;
import dev.zawarudo.holo.modules.MerriamWebsterClient;
import dev.zawarudo.holo.modules.emotes.EmoteManager;
import dev.zawarudo.holo.modules.xkcd.XkcdSyncService;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Creates instances of all the commands and manages them
 */
public class CommandManager extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private final Map<String, AbstractCommand> commands = new LinkedHashMap<>();
    private final Map<AbstractCommand, CommandModule.ModuleId> ownerModule = new IdentityHashMap<>();

    public CommandManager(
            EventWaiter waiter,
            ModuleRegistry moduleRegistry,
            GitHubClient gitHubClient,
            MerriamWebsterClient merriamWebsterClient,
            GuildConfigManager guildConfigManager,
            PermissionManager permissionManager,
            EmoteManager emoteManager,
            XkcdDao xkcdDao,
            XkcdSyncService xkcdSyncService,
            CountdownDao countdownDao
    ) {
        // General Cmds
        addCommand(new BugCmd(gitHubClient));
        addCommand(new ConfigCmd(guildConfigManager, moduleRegistry));
        addCommand(new HelpCmd(this));
        addCommand(new BotInfoCmd());
        addCommand(new PermCmd());
        addCommand(new PingCmd());
        addCommand(new RoleInfoCmd());
        addCommand(new ServerInfoCmd());
        addCommand(new ServerRolesCmd());
        addCommand(new SuggestionCmd(gitHubClient));
        addCommand(new WhoisCmd());

        // Anime Cmds
        addCommand(new AnimeSearchCmd(waiter));
        addCommand(new MangaSearchCmd(waiter));

        // Image Cmds
        addCommand(new ActionCmd());
        addCommand(new AoCStatsCmd());
        addCommand(new AvatarCmd());
        addCommand(new BannerCmd());
        addCommand(new CheckNSFWCmd());
        addCommand(new DogCmd());
        addCommand(new EmoteCmd(emoteManager));
        addCommand(new FilterCmd());
        addCommand(new HttpCmd());
        addCommand(new InspiroCmd());
        addCommand(new PaletteCmd());
        addCommand(new PixelateCmd());
        addCommand(new UpscaleCmd());
        addCommand(new XkcdCmd(xkcdDao, xkcdSyncService));

        // Misc Cmds
        addCommand(new CoinFlipCmd());
        addCommand(new CountdownCmd(countdownDao));
        addCommand(new Magic8BallCmd());
        addCommand(new UrbanDictionaryCmd(waiter));
        addCommand(new UwuCmd());

        // Owner Cmds
        addCommand(new BlacklistCmd(permissionManager));
        addCommand(new CancelCmd());
        addCommand(new DeleteCmd());
        addCommand(new EchoCmd());
        addCommand(new NicknameCmd());
        addCommand(new NukeCmd());
        addCommand(new RestartCmd());
        addCommand(new ShutdownCmd());
        addCommand(new StatusCmd());

        // Register module commands
        for (CommandModule m : moduleRegistry.all()) {
            m.register(this);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Loaded {} commands!", commands.values().stream().distinct().count());
        }
    }

    /**
     * Registers a command. Note that commands with the {@link Deactivated} annotation are ignored.
     *
     * @param cmd The command to register.
     */
    public void addCommand(@NotNull AbstractCommand cmd) {
        addCommand(cmd, null);
    }

    public void addCommand(@NotNull AbstractCommand cmd, @Nullable CommandModule.ModuleId moduleId) {
        // Ignore deactivated commands
        if (cmd.getClass().isAnnotationPresent(Deactivated.class)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("{} is deactivated.", cmd.getClass().getSimpleName());
            }
            return;
        }

        putKey(cmd.getName(), cmd);
        for (String alias : cmd.getAlias()) {
            putKey(alias, cmd);
        }

        if (moduleId != null) {
            CommandModule.ModuleId existing = ownerModule.putIfAbsent(cmd, moduleId);
            if (existing != null && existing != moduleId) {
                LOGGER.warn("Command {} already assigned to module {} (new: {})", cmd.getName(), existing, moduleId);
            }
        }
    }

    private void putKey(String key, AbstractCommand cmd) {
        AbstractCommand existing = commands.putIfAbsent(key, cmd);
        if (existing != null && existing != cmd) {
            LOGGER.warn("Command key '{}' already registered by {}. Ignoring {}",
                    key, existing.getClass().getSimpleName(), cmd.getClass().getSimpleName());
        }
    }

    public Optional<CommandModule.ModuleId> getModuleOf(@NotNull AbstractCommand cmd) {
        return Optional.ofNullable(ownerModule.get(cmd));
    }

    /**
     * Returns the command that matches the given name. To avoid null-check, use
     * {@link CommandManager#isValidName(String)} beforehand.
     *
     * @param name The name of the command.
     * @return The command that matches the given name, or <code>null</code> if no command matches.
     */
    public AbstractCommand getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Checks if given name is linked to a command.
     *
     * @param name The name to check.
     * @return True if a command exists with the given name, false otherwise.
     */
    public boolean isValidName(String name) {
        return commands.containsKey(name);
    }

    /**
     * Get every command of a given category as a list.
     *
     * @param category The {@link CommandCategory} to get the commands from.
     * @return A list of commands.
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