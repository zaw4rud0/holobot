package com.xharlock.holo.config;

import com.xharlock.holo.core.Bootstrap;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 * Represents the configuration of Holo inside a single {@link Guild}
 */
public class GuildConfig {
	
	private Role adminRole;
	private VoiceChannel musicChannel;
	private Role djRole;
	private String prefix;
	
	public Role getAdminRole() {
		return adminRole;
	}

	public void setAdminRole(Role adminRole) {
		this.adminRole = adminRole;
	}
	
	public VoiceChannel getMusicChannel() {
		return musicChannel;
	}
	
	public void setMusicChannel(VoiceChannel channel) {
		this.musicChannel = channel;
	}
	
	public Role getDJRole() {
		return djRole;
	}
	
	public void setDJRole(Role djRole) {
		this.djRole = djRole;
	}
	
	public String getGuildPrefix() {
		return prefix;
	}
	
	public void setGuildPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setDefaultPrefix() {
		this.prefix = Bootstrap.holo.getConfig().getDefaultPrefix();
	}
}