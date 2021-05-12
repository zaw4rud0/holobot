package com.xharlock.otakusenpai.core;

import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.core.CommandListener;
import com.xharlock.otakusenpai.commands.core.CommandManager;
import com.xharlock.otakusenpai.commands.core.Permission;
import com.xharlock.otakusenpai.config.Config;
import com.xharlock.otakusenpai.config.GuildConfigManager;
import com.xharlock.otakusenpai.misc.Misc;
import com.xharlock.otakusenpai.place.Place;

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
	private EventWaiter waiter;
	private Permission permission;

	private Place place;

	public OtakuSenpai(Config config, EventWaiter waiter) {
		this.config = config;
		this.waiter = waiter;
		this.permission = new Permission();
		init();
	}

	private void init() {
		System.out.println("Starting bot...");

		Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));

		try {
			place = new Place();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			JDABuilder builder = JDABuilder.createDefault(getConfig().getDiscordToken());
			builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
			builder.setChunkingFilter(ChunkingFilter.ALL);
			builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			builder.enableCache(CacheFlag.VOICE_STATE, new CacheFlag[0]);
			builder.addEventListeners(new ReadyListener(), this.waiter);
			builder.setActivity(Activity.watching(String.valueOf(this.config.getPrefix()) + "help"));
			this.jda = builder.build();
		} catch (LoginException ex) {
			ex.printStackTrace();
		}
	}

	public void registerListeners() {
		this.jda.addEventListener(new CommandListener(this.commandManager));
		this.jda.addEventListener(new Misc());
	}

	public void registerManagers() {
		this.guildConfigManager = new GuildConfigManager();
		this.commandManager = new CommandManager(this.waiter);
	}

	public Config getConfig() {
		return this.config;
	}

	public JDA getJDA() {
		return this.jda;
	}

	public Permission getPermission() {
		return this.permission;
	}

	public GuildConfigManager getGuildConfigManager() {
		return this.guildConfigManager;
	}

	public CommandManager getCommandManager() {
		return this.commandManager;
	}

	public Place getPlace() {
		return this.place;
	}
}
