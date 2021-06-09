package com.xharlock.holo.config;

public class Config {

	// Discord Properties
	private String discord_token;
	private long owner_id;

	// External stuff
	private String deepAI_key;

	// Minor bot properties
	private String default_prefix;
	private int default_color;
	private String version;

	public String getDiscordToken() {
		return this.discord_token;
	}

	public void setDiscordToken(String token) {
		this.discord_token = token;
	}
	
	public long getOwnerId() {
		return owner_id;
	}

	public void setOwnerId(long owner_id) {
		this.owner_id = owner_id;
	}
	
	public String getKeyDeepAI() {
		return deepAI_key;
	}

	public void setKeyDeepAI(String deepAI_key) {
		this.deepAI_key = deepAI_key;
	}
	
	public String getDefaultPrefix() {
		return this.default_prefix;
	}

	public void setDefaultPrefix(String prefix) {
		this.default_prefix = prefix;
	}
	
	public int getDefaultColor() {
		return this.default_color;
	}
	
	public void setDefaultColor(int color) {
		this.default_color = color;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}	
}
