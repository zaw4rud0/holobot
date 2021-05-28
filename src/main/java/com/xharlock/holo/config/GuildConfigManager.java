package com.xharlock.holo.config;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.Guild;

public class GuildConfigManager {

	private HashMap<Long, GuildConfig> guild_configs;
	
	public GuildConfigManager() {
		guild_configs = new HashMap<>();
	}
	
	public GuildConfig getGuildConfig(Guild guild) {
		if (!hasConfig(guild)) {
			GuildConfig guild_config = new GuildConfig();
			guild_config.setDefaultPrefix();
			guild_config.setDefaultColor();
			this.guild_configs.put(guild.getIdLong(), guild_config);
		}		
		return this.guild_configs.get(guild.getIdLong());
	}
	
	public boolean hasConfig(Guild guild) {
		return this.guild_configs.containsKey(guild.getIdLong());
	}
	
	@Deprecated
	public void setGuildConfigs(HashMap<Long, GuildConfig> guild_configs) {
		this.guild_configs = guild_configs;
	}
}
