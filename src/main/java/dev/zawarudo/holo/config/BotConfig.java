package dev.zawarudo.holo.config;

import com.google.gson.annotations.SerializedName;

import java.awt.*;

public class BotConfig {

	@SerializedName("token")
	private String botToken;
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
	@SerializedName("version")
	private String version;

	public String getBotToken() {
		return botToken;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public String getKeyDeepAI() {
		return deepAIKey;
	}

	public String getYoutubeToken() {
		return youtubeToken;
	}
	
	public String getAoCToken() {
		return aocToken;
	}

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public void setDefaultPrefix(String prefix) {
		this.defaultPrefix = prefix;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}