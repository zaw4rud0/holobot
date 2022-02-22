package com.xharlock.holo.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.commands.core.CommandListener;
import com.xharlock.holo.commands.core.CommandManager;
import com.xharlock.holo.commands.core.PermissionManager;
import com.xharlock.holo.config.Config;
import com.xharlock.holo.config.GuildConfigManager;
import com.xharlock.holo.games.pokemon.PokemonSpawnManager;
import com.xharlock.holo.misc.BotHandler;
import com.xharlock.holo.misc.Misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Holo {

	private JDA jda;
	private Config config;
	private GuildConfigManager guildConfigManager;
	private CommandManager commandManager;
	private PermissionManager permissionManager;
	private PokemonSpawnManager pokemonSpawnManager;
	private EventWaiter waiter;

	private static final Logger logger = LoggerFactory.getLogger(Holo.class);

	public Holo(Config config) throws LoginException {
		this.config = config;
		waiter = new EventWaiter();
		init();
	}

	private void init() throws LoginException {
		if (logger.isInfoEnabled()) {
			logger.info("Starting bot...");
		}

		// Add ShutdownThread to run code before the program shuts down
		Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));

		// Create a new JDA instance
		JDABuilder builder = JDABuilder.createDefault(getConfig().getDiscordToken());
		builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
		builder.setChunkingFilter(ChunkingFilter.ALL);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.enableCache(CacheFlag.VOICE_STATE, new CacheFlag[0]);
		builder.addEventListeners(new Listener(), waiter);
		builder.setActivity(Activity.watching(config.getDefaultPrefix() + "help"));
		jda = builder.build();
	}

	/**
	 * Initializes all the different managers
	 */
	public void registerManagers() {
		guildConfigManager = new GuildConfigManager();
		pokemonSpawnManager = new PokemonSpawnManager(jda, config.getPokemonChannels());
		commandManager = new CommandManager(waiter);
		permissionManager = new PermissionManager();
	}

	public void registerListeners() {
		jda.addEventListener(new CommandListener(commandManager), new BotHandler(), new Misc());
	}

	public JDA getJDA() {
		return jda;
	}

	public Config getConfig() {
		return config;
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
}

/** A simple listener that checks if Holo is ready */
class Listener extends ListenerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(Listener.class);

	@Override
	public void onReady(ReadyEvent e) {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("%s is ready!", e.getJDA().getSelfUser().getAsTag()));
		}
		Bootstrap.holo.registerManagers();
		Bootstrap.holo.registerListeners();
		
		// Delete messages from previous session
		deleteMessages();
	}
	
	private void deleteMessages() {
		String s = null;
		try {
			s = new String(Files.readAllBytes(Paths.get("delete.json")), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		MessageToDelete[] msgs = new Gson().fromJson(s, MessageToDelete[].class);

		for (MessageToDelete msg : msgs) {
			Bootstrap.holo.getJDA().getTextChannelById(msg.channelId).retrieveMessageById(msg.messageId).complete().delete().queue();
		}
	}
}