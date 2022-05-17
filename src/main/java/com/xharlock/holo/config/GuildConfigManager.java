package com.xharlock.holo.config;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages the configurations of the bot within a guild.
 */
public class GuildConfigManager {

	private final Map<Long, GuildConfig> guildConfigs;
	
	public GuildConfigManager() {
		guildConfigs = new HashMap<>();
	}

	/**
	 * Returns the bot configuration for the specified guild.
	 */
	public GuildConfig getGuildConfig(Guild guild) {
		if (!hasConfig(guild)) {
			guildConfigs.put(guild.getIdLong(), createNewConfig());
		}		
		return guildConfigs.get(guild.getIdLong());
	}

	/**
	 * Creates a new configuration with default settings.
	 */
	public GuildConfig createNewConfig() {
		GuildConfig guildConfig = new GuildConfig();
		guildConfig.setDefaultPrefix();
		guildConfig.setAllowNSFW(false);
		return guildConfig;
	}

	/**
	 * Checks if there are any configurations for the specified guild.
	 */
	public boolean hasConfig(Guild guild) {
		return guildConfigs.containsKey(guild.getIdLong());
	}
}