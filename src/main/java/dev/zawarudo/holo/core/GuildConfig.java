package dev.zawarudo.holo.core;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Represents the configuration of Holo for a specific {@link Guild}.
 */
public class GuildConfig {

	private final long guildId;
	private String prefix;
	private boolean nsfw;

	/**
	 * Creates a new GuildConfig instance with the default configurations.
	 */
	public GuildConfig(long guildId) {
		this.guildId = guildId;
		prefix = Bootstrap.holo.getConfig().getDefaultPrefix();
	}

	/**
	 * Returns the guild this bot configuration is for.
	 */
	public long getGuildId() {
		return guildId;
	}

	/**
	 * Returns the current prefix of the bot for this guild.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Changes the bot prefix for this guild to the specified string.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Reverts the prefix of the bot to the default one.
	 */
	public void revertPrefix() {
		prefix = Bootstrap.holo.getConfig().getDefaultPrefix();
	}

	/**
	 * Checks whether NSFW commands are allowed in this guild.
	 */
	public boolean isNSFWEnabled() {
		return nsfw;
	}

	/**
	 * Sets whether NSFW commands are allowed in this guild.
	 */
	public void setAllowNSFW(boolean nsfw) {
		this.nsfw = nsfw;
	}
}