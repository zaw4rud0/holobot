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
		builder.addEventListeners(new ReadyListener(), this.waiter);
		builder.setActivity(Activity.watching(this.config.getPrefix() + "help"));
		this.jda = builder.build();
	}

	public void registerManagers() {
		this.guildConfigManager = new GuildConfigManager();
		this.commandManager = new CommandManager(this.waiter);
		this.permissionManager = new PermissionManager();
	}

	public void registerListeners() {
		this.jda.addEventListener(new CommandListener(this.commandManager));
	}
	
	public Config getConfig() {
		return this.config;
	}

	public JDA getJDA() {
		return this.jda;
	}
	
	public GuildConfigManager getGuildConfigManager() {
		return this.guildConfigManager;
	}

	public CommandManager getCommandManager() {
		return this.commandManager;
	}

	public PermissionManager getPermissionManager() {
		return this.permissionManager;
	}
}
