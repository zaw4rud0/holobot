package dev.zawarudo.holo.core;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.commands.CommandListener;
import dev.zawarudo.holo.commands.CommandManager;
import dev.zawarudo.holo.commands.games.pokemon.PokemonSpawnManager;
import dev.zawarudo.holo.core.misc.GuildListener;
import dev.zawarudo.holo.core.misc.MiscListener;
import dev.zawarudo.holo.database.SQLManager;
import dev.zawarudo.holo.modules.GitHubClient;
import dev.zawarudo.holo.modules.emotes.EmoteManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;

/**
 * Represents an instance of the bot.
 */
public class Holo extends ListenerAdapter {

	private JDA jda;
	private final BotConfig botConfig;
	private GuildConfigManager guildConfigManager;
	private CommandManager commandManager;
	private PermissionManager permissionManager;
	private PokemonSpawnManager pokemonSpawnManager;
	private SQLManager sqlManager;
	private GitHubClient gitHubClient;
	private EmoteManager emoteManager;

	private final EventWaiter waiter;

	private static final Logger LOGGER = LoggerFactory.getLogger(Holo.class);

	public Holo(BotConfig botConfig) {
		this.botConfig = botConfig;
		waiter = new EventWaiter();
		init();
	}

	private void init() {
		LOGGER.info("Starting bot...");

		// Create a new JDA instance
		JDABuilder builder = JDABuilder.createDefault(getConfig().getBotToken());
		builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
		builder.setChunkingFilter(ChunkingFilter.ALL);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.enableCache(CacheFlag.VOICE_STATE);
		builder.addEventListeners(this, waiter);
		builder.setActivity(Activity.watching(botConfig.getDefaultPrefix() + "help"));
		jda = builder.build();
	}

	public void registerManagers() {
		try {
			sqlManager = new SQLManager();
			gitHubClient = new GitHubClient(botConfig.getGitHubToken());
		} catch (IOException e) {
			throw new IllegalStateException("Something went wrong while registering managers.", e);
		}

		emoteManager = new EmoteManager();
		guildConfigManager = new GuildConfigManager();
		pokemonSpawnManager = new PokemonSpawnManager(jda);

		// Warning: Commands only get initialized here
		commandManager = new CommandManager(waiter);
		permissionManager = new PermissionManager();
	}

	public void registerListeners() {
		jda.addEventListener(
				new CommandListener(commandManager, permissionManager),
				new MiscListener(),
				new GuildListener(guildConfigManager)
		);
	}

	public JDA getJDA() {
		return jda;
	}

	public BotConfig getConfig() {
		return botConfig;
	}

	public GuildConfigManager getGuildConfigManager() {
		return guildConfigManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public PokemonSpawnManager getPokemonSpawnManager() {
		return pokemonSpawnManager;
	}

	public SQLManager getSQLManager() {
		return sqlManager;
	}

	public GitHubClient getGitHubClient() {
		return gitHubClient;
	}

	public EmoteManager getEmoteManager() {
		return emoteManager;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		Bootstrap.holo.registerManagers();
		Bootstrap.holo.registerListeners();

		LOGGER.info("{} is ready!", event.getJDA().getSelfUser().getName());
	}
}
