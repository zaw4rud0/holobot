package com.xharlock.holo.config;

import java.awt.Color;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Config {

	@SerializedName("token")
	private String discordToken;
	@SerializedName("owner_id")
	private long ownerId;

	// External stuff
	@SerializedName("deepAI_token")
	private String deepAIKey;
	@SerializedName("youtube_token")
	private String youtubeToken;
	@SerializedName("aoc_token")
	private String aocToken;

	// Minor bot properties
	@SerializedName("default_prefix")
	private String defaultPrefix;
	@SerializedName("default_color")
	private int defaultColor;
	@SerializedName("version")
	private String version;

	/** TextChannels where Pokemon should spawn */
	@SerializedName("pokemon_channels")
	private List<Long> channels;

	public String getDiscordToken() {
		return discordToken;
	}

	public void setDiscordToken(String token) {
		this.discordToken = token;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public String getKeyDeepAI() {
		return deepAIKey;
	}

	public void setKeyDeepAI(String deepAIKey) {
		this.deepAIKey = deepAIKey;
	}

	public String getYoutubeToken() {
		return youtubeToken;
	}

	public void setYoutubeToken(String youtubeToken) {
		this.youtubeToken = youtubeToken;
	}
	
	public String getAoCToken() {
		return aocToken;
	}
	
	public void setAoCToken(String aocToken) {
		this.aocToken = aocToken;
	}

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public void setDefaultPrefix(String prefix) {
		this.defaultPrefix = prefix;
	}

	public Color getDefaultColor() {
		return new Color(defaultColor);
	}

	public void setDefaultColor(int color) {
		this.defaultColor = color;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Long> getPokemonChannels() {
		return channels;
	}

	public void setPokemonChannels(List<Long> channels) {
		this.channels = channels;
	}
}
