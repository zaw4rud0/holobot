package com.xharlock.otakusenpai.config;

import com.xharlock.otakusenpai.core.Bootstrap;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class GuildConfig {
	
	private Role admin_role;
	private VoiceChannel music_channel;
	private Role dj_role;
	private String prefix;
	private int embed_color;
	
	public Role getAdminRole() {
		return this.admin_role;
	}

	public void setAdminRole(Role admin_role) {
		this.admin_role = admin_role;
	}
	
	public VoiceChannel getMusicChannel() {
		return this.music_channel;
	}
	
	public void setMusicChannel(VoiceChannel channel) {
		this.music_channel = channel;
	}
	
	public Role getDJRole() {
		return this.dj_role;
	}
	
	public void setDJRole(Role dj_role) {
		this.dj_role = dj_role;
	}
	
	public String getGuildPrefix() {
		return this.prefix;
	}
	
	public void setGuildPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public int getEmbedColor() {
		return this.embed_color;
	}
	
	public void setEmbedColor(int embed_color) {
		this.embed_color = embed_color;
	}
	
	public void setDefaultPrefix() {
		this.prefix = Bootstrap.otakuSenpai.getConfig().getPrefix();
	}
	
	public void setDefaultColor() {
		this.embed_color = Bootstrap.otakuSenpai.getConfig().getColor();
	}
}
