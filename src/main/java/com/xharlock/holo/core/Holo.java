package com.xharlock.holo.core;

import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.commands.core.CommandListener;
import com.xharlock.holo.commands.core.CommandManager;
import com.xharlock.holo.commands.core.PermissionManager;
import com.xharlock.holo.config.Config;
import com.xharlock.holo.config.GuildConfigManager;
import com.xharlock.holo.experimental.ButtonEventListener;
import com.xharlock.holo.misc.BotHandler;
import com.xharlock.holo.misc.Misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
	private EventWaiter waiter;
	
	private static final Logger logger = LoggerFactory.getLogger(Holo.class);

	public Holo(Config config, EventWaiter waiter) throws LoginException {		
		this.config = config;
		this.waiter = waiter;
		init();
	}

	private void init() throws LoginException {		
		logger.info("Starting bot...");
		Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
		
		// Create new JDA instance
		JDABuilder builder = JDABuilder.createDefault(getConfig().getDiscordToken());
		builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
		builder.setChunkingFilter(ChunkingFilter.ALL);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.enableCache(CacheFlag.VOICE_STATE, new CacheFlag[0]);
		builder.addEventListeners(new Listener(), waiter);
		builder.setActivity(Activity.watching(config.getDefaultPrefix() + "help"));
		jda = builder.build();
	}

	public void registerManagers() {
		guildConfigManager = new GuildConfigManager();
		commandManager = new CommandManager(waiter);
		permissionManager = new PermissionManager();
	}

	public void registerListeners() {
		jda.addEventListener(
			new CommandListener(commandManager), 
			new BotHandler(),
			new Misc(),
			new ButtonEventListener()
		);
	}
	
	public Config getConfig() {
		return config;
	}

	public JDA getJDA() {
		return jda;
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
}
