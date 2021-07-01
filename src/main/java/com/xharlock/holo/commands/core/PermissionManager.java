package com.xharlock.holo.commands.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.core.Bootstrap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Invite.Channel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Class that handles what an user is allowed to do
 */
public class PermissionManager {

	// In dire need of refactoring
	
	private List<User> blacklisted;
	private HashMap<User, Long> lastUserWarning;

	public PermissionManager() {
		blacklisted = new ArrayList<>();
		lastUserWarning = new HashMap<>();
	}

	public boolean check(MessageReceivedEvent e, Command cmd) {
		if (isBlacklisted(e.getAuthor()))
			return false;

		if (!hasChannelPermission(e, cmd))
			return false;

		if (!hasUserPermission(e, cmd))
			return false;

		if (cmd.hasCmdCooldown())
			if (isUserOnCooldown(e, cmd))
				return false;

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

	public boolean isUserOnCooldown(MessageReceivedEvent e, Command cmd) {
		
		e.getMessage().delete().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		long now = Instant.now().getEpochSecond();
		if (cmd.onTimeout.containsKey(e.getAuthor())) {			
			boolean warned = this.lastUserWarning.containsKey(e.getAuthor());			
			if (warned && now - this.lastUserWarning.get(e.getAuthor()) < 10L)
				return true;			
			if (now - cmd.onTimeout.get(e.getAuthor()) < cmd.getCmdCooldown()) {
				if (e.isFromGuild())
					e.getMessage().delete().queue();
				builder.setTitle("On Cooldown!");
				int remaining = cmd.getCmdCooldown() - (int) (now - cmd.onTimeout.get(e.getAuthor()));
				builder.setDescription(String.format("%s, you are on cooldown!\nPlease wait `%d` seconds before using this command again.", e.getAuthor().getAsMention(), remaining));
				cmd.sendEmbed(e, builder, 10, TimeUnit.SECONDS, false);
				
				if (warned) 
					this.lastUserWarning.replace(e.getAuthor(), now);
				else 
					this.lastUserWarning.put(e.getAuthor(), now);
				
				return true;
			} else {
				cmd.onTimeout.replace(e.getAuthor(), now);
				return false;
			}
		} else {
			cmd.onTimeout.put(e.getAuthor(), now);
			return false;
		}
	}

	public boolean onChannelOnCooldown(Channel channel, Command cmd) {
		return false;
	}

	public boolean hasChannelPermission(MessageReceivedEvent e, Command cmd) {
		EmbedBuilder builder = new EmbedBuilder();
		if (e.isFromType(ChannelType.PRIVATE) && cmd.isGuildOnlyCommand()) {
			cmd.addErrorReaction(e.getMessage());
			builder.setTitle("No Permission");
			builder.setDescription("You are not allowed to use this command in a private chat!");
			cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return false;
		}
		if (e.isFromGuild() && !e.getTextChannel().isNSFW() && cmd.isNSFW()) {
			cmd.addErrorReaction(e.getMessage());
			builder.setTitle("NSFW Command");
			builder.setDescription("You can't use a NSFW command in a non-NSFW channel, p-pervert!");
			cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return false;
		}
		return true;
	}

	public boolean hasUserPermission(MessageReceivedEvent e, Command cmd) {
		EmbedBuilder builder = new EmbedBuilder();

		// Checks if user is bot-owner and can use owner-only commands
		if (cmd.isOwnerCommand() && e.getAuthor().getIdLong() != Bootstrap.holo.getConfig().getOwnerId()) {
			cmd.addErrorReaction(e.getMessage());
			builder.setTitle("No Permission");
			builder.setDescription("This command is owner-only");
			cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return false;
		}

		// Checks if user is an admin or server-owner and if he can use admin commands
		if (e.isFromGuild()) {
			Role admin = Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getAdminRole();
			if (admin == null) {
				if (cmd.isAdminCommand() && !e.getGuild().getOwner().getUser().equals(e.getAuthor())) {
					cmd.addErrorReaction(e.getMessage());
					builder.setTitle("No Permission");
					builder.setDescription("This command is admin-only");
					cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
					return false;
				}
			} else {
				if (cmd.isAdminCommand() && !e.getGuild().getOwner().getUser().equals(e.getAuthor())
						&& !e.getGuild().getMember(e.getAuthor()).getRoles().contains(admin)) {
					cmd.addErrorReaction(e.getMessage());
					builder.setTitle("No Permission");
					builder.setDescription("This command is admin-only");
					cmd.sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
					return false;
				}
			}
		}
		return true;
	}
}
