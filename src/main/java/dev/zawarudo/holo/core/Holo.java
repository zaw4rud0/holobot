package dev.zawarudo.holo.core;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.config.BotConfig;
import dev.zawarudo.holo.config.GuildConfigManager;
import dev.zawarudo.holo.games.akinator.AkinatorManager;
import dev.zawarudo.holo.games.pokemon.PokemonSpawnManager;
import dev.zawarudo.holo.misc.BotHandler;
import dev.zawarudo.holo.misc.GuildListener;
import dev.zawarudo.holo.misc.MiscListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.EnumSet;

public class Holo extends ListenerAdapter {

	private JDA jda;
	private final BotConfig botConfig;
	private GuildConfigManager guildConfigManager;
	private CommandManager commandManager;
	private PermissionManager permissionManager;
	private PokemonSpawnManager pokemonSpawnManager;
	private AkinatorManager akinatorManager;
	private final EventWaiter waiter;

	private static final Logger logger = LoggerFactory.getLogger(Holo.class);

	public Holo(BotConfig botConfig) throws LoginException {
		this.botConfig = botConfig;
		waiter = new EventWaiter();
		init();
	}

	private void init() throws LoginException {
		if (logger.isInfoEnabled()) {
			logger.info("Starting bot...");
		}

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

	/**
	 * Initializes all the different managers
	 */
	public void registerManagers() {
		guildConfigManager = new GuildConfigManager();
		pokemonSpawnManager = new PokemonSpawnManager(jda, new ArrayList<>());
		akinatorManager = new AkinatorManager(waiter);
		commandManager = new CommandManager(waiter);
		permissionManager = new PermissionManager();
	}

	public void registerListeners() {
		jda.addEventListener(
				new CommandListener(commandManager, permissionManager),
				new BotHandler(),
				new MiscListener(),
				new GuildListener());
	}

	/**
	 * Returns the JDA instance
	 */
	public JDA getJDA() {
		return jda;
	}

	/**
	 * Returns the configurations of the bot
	 */
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
	
	public AkinatorManager getAkinatorManager() {
		return akinatorManager;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("%s is ready!", event.getJDA().getSelfUser().getAsTag()));
		}
		Bootstrap.holo.registerManagers();
		Bootstrap.holo.registerListeners();
	}
}