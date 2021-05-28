package com.xharlock.otakusenpai.core;

import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.core.CommandListener;
import com.xharlock.otakusenpai.commands.core.CommandManager;
import com.xharlock.otakusenpai.commands.core.PermissionManager;
import com.xharlock.otakusenpai.config.Config;
import com.xharlock.otakusenpai.config.GuildConfigManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class OtakuSenpai {

	private JDA jda;

	private Config config;
	private GuildConfigManager guildConfigManager;
	private CommandManager commandManager;
	private PermissionManager permissionManager;
	private EventWaiter waiter;

	public OtakuSenpai(Config config, EventWaiter waiter) throws LoginException {
		this.config = config;
		this.waiter = waiter;
		init();
	}

	private void init() throws LoginException {
		System.out.println("Starting bot...");
		Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
		JDABuilder builder = JDABuilder.createDefault(getConfig().getDiscordToken());
		builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
		builder.setChunkingFilter(ChunkingFilter.ALL);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.enableCache(CacheFlag.VOICE_STATE, new CacheFlag[0]);
		builder.addEventListeners(new ReadyListener(), this.waiter);
		builder.setActivity(Activity.watching(String.valueOf(this.config.getPrefix()) + "help"));
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
