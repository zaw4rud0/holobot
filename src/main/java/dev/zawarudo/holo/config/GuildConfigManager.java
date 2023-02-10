package dev.zawarudo.holo.config;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages the configurations of the bot within each guild.
 */
public class GuildConfigManager {

	private final Map<Long, GuildConfig> guildConfigs;
	
	public GuildConfigManager() {
		guildConfigs = new HashMap<>();
	}

	/**
	 * Returns the bot configuration for the specified guild.
	 *
	 * @param guild The guild to get the configuration for.
	 * @return The configuration for the specified guild.
	 */
	public GuildConfig getGuildConfig(Guild guild) {
		if (!hasGuildConfig(guild)) {
			guildConfigs.put(guild.getIdLong(), new GuildConfig());
		}
		return guildConfigs.get(guild.getIdLong());
	}

	/**
	 * Checks if there are any configurations for the specified guild.
	 *
	 * @param guild The guild to check for configurations.
	 * @return True if there are configurations for the specified guild, false otherwise.
	 */
	public boolean hasGuildConfig(Guild guild) {
		return guildConfigs.containsKey(guild.getIdLong());
	}
}