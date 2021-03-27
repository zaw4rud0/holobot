package com.xharlock.otakusenpai.commands.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.core.Main;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Invite.Channel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Permission {

	private List<User> blacklisted;
	private HashMap<User, Long> lastUserWarning;

	public Permission() {
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

	public boolean isBlacklisted(User user) {
		return blacklisted.contains(user);
	}

	public void blacklist(User user) {
		blacklisted.add(user);
	}

	@Deprecated
	// Write these methods in the Command Class so you can tweak them for each command
	public boolean isUserOnCooldown(MessageReceivedEvent e, Command cmd) {
		EmbedBuilder builder = new EmbedBuilder();
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		long now = Instant.now().getEpochSecond();
		if (cmd.onTimeout.containsKey(e.getAuthor())) {			
			boolean warned = this.lastUserWarning.containsKey(e.getAuthor());			
			if (warned && now - this.lastUserWarning.get(e.getAuthor()) < 10L)
				return true;			
			if (now - cmd.onTimeout.get(e.getAuthor()) < cmd.getCmdCooldown()) {
				builder.setTitle(Messages.TITLE_ON_COOLDOWN.getText());
				int remaining = cmd.getCmdCooldown() - (int) (now - cmd.onTimeout.get(e.getAuthor()));
				builder.setDescription(Messages.CMD_USER_ON_COOLDOWN.getText().replace("{0}", e.getAuthor().getAsMention())
						.replace("{1}", String.valueOf(remaining)));
				cmd.sendEmbed(e, builder, 10L, TimeUnit.SECONDS, false);				
				if (warned) this.lastUserWarning.replace(e.getAuthor(), now);
				else this.lastUserWarning.put(e.getAuthor(), now);				
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
			builder.setTitle(Messages.TITLE_ERROR.getText());
			builder.setDescription(Messages.NO_PRIVATE_CHAT_PERM.getText());
			cmd.sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
			return false;
		}
		if (e.isFromGuild() && !e.getTextChannel().isNSFW() && cmd.isNSFW()) {
			cmd.addErrorReaction(e.getMessage());
			builder.setTitle(Messages.TITLE_CMD_NSFW.getText());
			builder.setDescription(Messages.NO_NSFW_CHANNEL_PERM.getText());
			cmd.sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
			return false;
		}
		return true;
	}

	public boolean hasUserPermission(MessageReceivedEvent e, Command cmd) {
		EmbedBuilder builder = new EmbedBuilder();

		// Checks if user is bot-owner and can use owner-only commands
		if (cmd.isOwnerCommand() && e.getAuthor().getIdLong() != Main.otakuSenpai.getConfig().getOwnerId()) {
			cmd.addErrorReaction(e.getMessage());
			builder.setTitle(Messages.TITLE_NO_PERM.getText());
			builder.setDescription(Messages.CMD_OWNER_ONLY.getText());
			cmd.sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
			return false;
		}

		if (e.isFromGuild()) {
			Role admin = Main.otakuSenpai.getGuildConfigManager().getGuildConfig(e.getGuild()).getAdminRole();
			if (admin == null) {
				if (cmd.isAdminCommand() && !e.getGuild().getOwner().getUser().equals(e.getAuthor())) {
					cmd.addErrorReaction(e.getMessage());
					builder.setTitle(Messages.TITLE_NO_PERM.getText());
					builder.setDescription(Messages.CMD_ADMIN_ONLY.getText());
					cmd.sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
					return false;
				}
			} else {
				if (cmd.isAdminCommand() && !e.getGuild().getOwner().getUser().equals(e.getAuthor())
						&& !e.getGuild().getMember(e.getAuthor()).getRoles().contains(admin)) {
					cmd.addErrorReaction(e.getMessage());
					builder.setTitle(Messages.TITLE_NO_PERM.getText());
					builder.setDescription(Messages.CMD_ADMIN_ONLY.getText());
					cmd.sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
					return false;
				}
			}
		}
		return true;
	}
}
