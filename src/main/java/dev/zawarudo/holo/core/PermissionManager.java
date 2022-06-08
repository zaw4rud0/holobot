package dev.zawarudo.holo.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles what a user is allowed to do
 *
 * TODO: Rework everything
 */
public class PermissionManager {

	private final List<User> blacklisted;

	public PermissionManager() {
		blacklisted = new ArrayList<>();
	}

	public boolean check(MessageReceivedEvent e, AbstractCommand cmd) {
		if (isBlacklisted(e.getAuthor())) {
			return false;
		}
		if (!hasChannelPermission(e, cmd)) {
			return false;
		}
		if (!hasUserPermission(e, cmd)) {
			return false;
		}
		return true;
	}

	// TODO Make a db request
	public boolean isBlacklisted(User user) {
		return blacklisted.contains(user);
	}

	// TODO Write blacklisted user to db
	public void blacklist(User user) {
		blacklisted.add(user);
	}

	@Deprecated
	public boolean hasChannelPermission(MessageReceivedEvent e, AbstractCommand cmd) {
		EmbedBuilder builder = new EmbedBuilder();
		if (e.isFromType(ChannelType.PRIVATE) && cmd.isGuildOnly()) {
			builder.setTitle("No Permission");
			builder.setDescription("You are not allowed to use this command in a direct chat!");
			cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return false;
		}

		if (!nsfwCheck(e, cmd)) {
			builder.setTitle("NSFW Command");
			builder.setDescription("You can't use a NSFW command in a non-NSFW channel, p-pervert!");
			cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return false;
		}
		return true;
	}

	@Deprecated
	public boolean hasUserPermission(MessageReceivedEvent e, AbstractCommand cmd) {
		EmbedBuilder builder = new EmbedBuilder();

		// Checks if user is bot-owner and can use owner-only commands
		if (cmd.isOwnerOnly() && e.getAuthor().getIdLong() != Bootstrap.holo.getConfig().getOwnerId()) {
			builder.setTitle("No Permission");
			builder.setDescription("This command is owner-only");
			cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return false;
		}

		// Checks if user is an admin or server-owner and if he can use admin commands
		if (e.isFromGuild()) {
			Role admin = Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getAdminRole();
			if (admin == null) {
				if (cmd.isAdminOnly() && !e.getGuild().getOwner().getUser().equals(e.getAuthor())) {
					builder.setTitle("No Permission");
					builder.setDescription("This command is admin-only");
					cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
					return false;
				}
			} else {
				if (cmd.isAdminOnly() && !e.getGuild().getOwner().getUser().equals(e.getAuthor()) && !e.getGuild().getMember(e.getAuthor()).getRoles().contains(admin)) {
					builder.setTitle("No Permission");
					builder.setDescription("This command is admin-only");
					cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
					return false;
				}
			}
		}
		return true;
	}

	/** Checks if a command can be used in terms of NSFW */
	private boolean nsfwCheck(MessageReceivedEvent e, AbstractCommand cmd) {
		// Command is SFW, no need to check further
		if (!cmd.isNSFW()) {
			return true;
		}

		// Check if parent channel of thread is marked as NSFW
		if (e.isFromThread()) {
			TextChannel channel = (TextChannel) e.getThreadChannel().getParentChannel();
			return channel.isNSFW();
		}

		// Check if channel is marked as NSFW
		if (e.isFromGuild()) {
			return e.getTextChannel().isNSFW();
		}

		// Private channel
		else {
			return true;
		}
	}
}