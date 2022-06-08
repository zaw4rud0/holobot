package dev.zawarudo.holo.config;

import dev.zawarudo.holo.core.Bootstrap;
import net.dv8tion.jda.api.entities.Guild;
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
	private boolean nsfw;

	private boolean autoDelete;

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

	@Deprecated
	public Role getDJRole() {
		return djRole;
	}

	@Deprecated
	public void setDJRole(Role djRole) {
		this.djRole = djRole;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setGuildPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setDefaultPrefix() {
		this.prefix = Bootstrap.holo.getConfig().getDefaultPrefix();
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

	/**
	 * Checks whether the bot should auto-delete its messages.
	 */
	public boolean isAutoDeleteEnabled() {
		return autoDelete;
	}

	/**
	 * Sets whether the bot should auto-delete its messages.
	 */
	public void setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
	}
}