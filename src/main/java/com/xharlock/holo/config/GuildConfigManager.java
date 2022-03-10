package com.xharlock.holo.config;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;

public class GuildConfigManager {

	private Map<Long, GuildConfig> guildConfigs;
	
	public GuildConfigManager() {
		guildConfigs = new HashMap<>();
	}
	
	public GuildConfig getGuildConfig(Guild guild) {
		if (!hasConfig(guild)) {
			GuildConfig guildConfig = new GuildConfig();
			guildConfig.setDefaultPrefix();
			guildConfigs.put(guild.getIdLong(), guildConfig);
		}		
		return guildConfigs.get(guild.getIdLong());
	}
	
	public boolean hasConfig(Guild guild) {
		return guildConfigs.containsKey(guild.getIdLong());
	}
}