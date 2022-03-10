package com.xharlock.holo.commands.core;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.core.Bootstrap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Abstract class representing a bot command
 */
public abstract class Command {

	protected String name;
	protected String description;
	protected String usage;
	protected String example;
	protected List<String> aliases = new ArrayList<>();
	protected String thumbnailUrl;
	protected Color embedColor = getDefaultColor();
	protected boolean isOwnerCommand = false;
	protected boolean isAdminCommand = false;
	protected boolean isNSFW = false;
	protected boolean isGuildOnly = false;

	protected String[] args;

	/** Cooldown in seconds */
	protected int cooldownDuration = 0;
	protected Map<User, Long> onTimeout = new HashMap<>();

	protected CommandCategory category = CommandCategory.BLANK;

	public Command(String name) {
		this.name = name;
	}

	public abstract void onCommand(MessageReceivedEvent e);

	protected String getPrefix(MessageReceivedEvent e) {
		if (e.isFromGuild()) {
			return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getGuildPrefix();
		} else {
			return Bootstrap.holo.getConfig().getDefaultPrefix();
		}
	}

	/** Returns the default color of the bot */
	protected Color getDefaultColor() {
		return Bootstrap.holo.getConfig().getDefaultColor();
	}

	// TODO
	protected boolean isGuildAdmin(MessageReceivedEvent e) {
		return false;
	}

	/**
	 * Checks if the user is the owner of this bot instance
	 */
	protected boolean isBotOwner(MessageReceivedEvent e) {
		return e.getAuthor().getIdLong() == Bootstrap.holo.getConfig().getOwnerId();
	}

	protected void sendEmbed(MessageReceivedEvent e, EmbedBuilder builder, boolean footer) {
		builder.setColor(getDefaultColor());
		if (e.isFromGuild() && footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		e.getChannel().sendMessageEmbeds(builder.build()).queue();
	}

	protected void sendEmbed(MessageReceivedEvent e, EmbedBuilder builder, boolean footer, Color color) {
		builder.setColor(color);
		if (e.isFromGuild() && footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		e.getChannel().sendMessageEmbeds(builder.build()).queue();
	}

	protected void sendEmbed(MessageReceivedEvent e, EmbedBuilder builder, long delay, TimeUnit unit, boolean footer) {
		builder.setColor(getDefaultColor());

		if (e.isFromGuild()) {
			if (footer) {
				builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
			}
			e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> {
				msg.delete().queueAfter(delay, unit);
			});
		} else {
			e.getChannel().sendMessageEmbeds(builder.build()).queue();
		}
	}

	protected void sendEmbed(MessageReceivedEvent e, EmbedBuilder builder, long delay, TimeUnit unit, boolean footer, Color color) {
		builder.setColor(color);

		if (e.isFromGuild()) {
			if (footer) {

			}
			e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> {
				msg.delete().queueAfter(delay, unit);
			});
		} else {
			e.getChannel().sendMessageEmbeds(builder.build()).queue();
		}
	}

	protected void sendReplyEmbed(MessageReceivedEvent e, EmbedBuilder builder, boolean footer) {
		builder.setColor(getDefaultColor());
		if (e.isFromGuild() && footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		e.getMessage().getReferencedMessage().replyEmbeds(builder.build()).queue();
	}

	protected void sendReplyEmbed(MessageReceivedEvent e, EmbedBuilder builder, long delay, TimeUnit unit, boolean footer) {
		builder.setColor(getDefaultColor());

		if (e.isFromGuild()) {
			if (footer) {
				builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
			}
			e.getMessage().getReferencedMessage().replyEmbeds(builder.build()).queue(msg -> {
				msg.delete().queueAfter(delay, unit);
			});
		} else {
			e.getMessage().getReferencedMessage().replyEmbeds(builder.build()).queue();
		}
	}

	protected Message sendEmbedAndGetMessage(MessageReceivedEvent e, EmbedBuilder builder, boolean footer) {
		builder.setColor(getDefaultColor());
		if (e.isFromGuild() && footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		return e.getChannel().sendMessageEmbeds(builder.build()).complete();
	}

	protected void sendToOwner(MessageReceivedEvent e, EmbedBuilder builder) {
		e.getJDA().getUserById(Bootstrap.holo.getConfig().getOwnerId()).openPrivateChannel().complete().sendMessageEmbeds(builder.build()).queue();
	}

	/**
	 * Deletes the message that invoked this command, if possible.
	 */
	protected void deleteInvoke(MessageReceivedEvent e) {
		if (e.isFromGuild()) {
			e.getMessage().delete().queue();
		}
	}

	protected void sendTyping(MessageReceivedEvent e) {
		e.getChannel().sendTyping().queue();
	}

	public String getName() {
		return name;
	}
	
	public CommandCategory getCommandCategory() {
		return category;
	}

	public void setCommandCategory(CommandCategory category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	public String getUsage() {
		return usage;
	}

	protected void setUsage(String usage) {
		this.usage = usage;
	}

	public String getExample() {
		return example;
	}

	protected void setExample(String example) {
		this.example = example;
	}

	public String getThumbnail() {
		return thumbnailUrl;
	}

	protected void setThumbnail(String url) {
		thumbnailUrl = url;
	}

	public Color getEmbedColor() {
		return embedColor;
	}

	protected void setEmbedColor(Color color) {
		embedColor = color;
	}

	public List<String> getAliases() {
		return aliases;
	}

	protected void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	public boolean isOwnerCommand() {
		return isOwnerCommand;
	}

	protected void setIsOwnerCommand(boolean isOwnerCommand) {
		this.isOwnerCommand = isOwnerCommand;
	}

	public boolean isAdminCommand() {
		return isAdminCommand;
	}

	protected void setIsAdminCommand(boolean isAdminCommand) {
		this.isAdminCommand = isAdminCommand;
	}

	public boolean isNSFW() {
		return isNSFW;
	}

	protected void setIsNSFW(boolean isNSFW) {
		this.isNSFW = isNSFW;
	}

	public boolean isGuildOnlyCommand() {
		return isGuildOnly;
	}

	protected void setIsGuildOnlyCommand(boolean isGuildOnlyCommand) {
		this.isGuildOnly = isGuildOnlyCommand;
	}

	public boolean hasCmdCooldown() {
		return cooldownDuration != 0;
	}

	public int getCmdCooldown() {
		return cooldownDuration;
	}

	protected void setCmdCooldown(int seconds) {
		this.cooldownDuration = seconds;
	}

	protected String[] getArgs() {
		return args;
	}

	/**
	 * Checks whether a String is a numeric
	 */
	protected boolean isNumber(String s) {
		if (s == null) {
			return false;
		}
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	protected boolean isValidURL(String url) {
		try {
			new URL(url).openStream().close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
