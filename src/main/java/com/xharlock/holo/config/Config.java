package com.xharlock.holo.config;

public class Config {

	// Discord Properties
	private String discordToken;
	private long ownerId;

	// External stuff
	private String deepAIKey;
	private String youtubeToken;

	// Minor bot properties
	private String defaultPrefix;
	private int defaultColor;
	private String version;

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
	
	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public void setDefaultPrefix(String prefix) {
		this.defaultPrefix = prefix;
	}
	
	public int getDefaultColor() {
		return defaultColor;
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
}
