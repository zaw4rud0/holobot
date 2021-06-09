package com.xharlock.holo.commands.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.misc.Emojis;

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
	protected String[] args;

	protected boolean isOwnerCommand = false;
	protected boolean isAdminCommand = false;
	protected boolean isNSFW = false;
	protected boolean isGuildOnly = false;

	/** Cooldown in seconds */
	protected int cooldownDuration = 0;
	protected HashMap<User, Long> onTimeout = new HashMap<>();

	protected CommandCategory category = CommandCategory.BLANK;

	public Command(String name) {
		this.name = name;
	}

	public abstract void onCommand(MessageReceivedEvent e);

	protected void addSuccessReaction(Message msg) {
		msg.addReaction(Emojis.THUMBSUP.getAsBrowser());
	}

	protected void addErrorReaction(Message msg) {
		msg.addReaction(Emojis.THUMBSDOWN.getAsBrowser());
	}

	protected String getPrefix(MessageReceivedEvent e) {
		if (e.isFromGuild())
			return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getGuildPrefix();
		else
			return Bootstrap.holo.getConfig().getDefaultPrefix();
	}

	/**
	 * Method to get the embed color of a guild or the default bot embed color if it's in a private channel
	 */
	protected int getColor(MessageReceivedEvent e) {
		if (e.isFromGuild())
			return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getEmbedColor();
		else
			return Bootstrap.holo.getConfig().getDefaultColor();
	}

	protected boolean isValidURL(String url) {
		try {
			new URL(url).openStream().close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	// TODO
	protected boolean isGuildAdmin(MessageReceivedEvent e) {
		return false;
	}
	
	protected boolean isBotOwner(MessageReceivedEvent e) {
		if (e.getAuthor().getIdLong() == Bootstrap.holo.getConfig().getOwnerId())
			return true;
		else
			return false;
	}

	protected void sendEmbed(MessageReceivedEvent e, EmbedBuilder builder, boolean footer) {
		builder.setColor(getColor(e));
		
		if (e.isFromGuild() && footer)
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		
		e.getChannel().sendMessage(builder.build()).queue();
	}

	protected void sendEmbed(MessageReceivedEvent e, EmbedBuilder builder, long delay, TimeUnit unit, boolean footer) {
		builder.setColor(getColor(e));
		
		if (e.isFromGuild()) {
			if (footer) {
				builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()),	e.getAuthor().getEffectiveAvatarUrl());
			}	
			e.getChannel().sendMessage(builder.build()).queue(msg -> {
				msg.delete().queueAfter(delay, unit);
			});
		} else {
			e.getChannel().sendMessage(builder.build()).queue();
		}
	}

	protected void sendReplyEmbed(MessageReceivedEvent e, EmbedBuilder builder, boolean footer) {
		builder.setColor(getColor(e));
		
		if (e.isFromGuild() && footer)
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		
		e.getMessage().getReferencedMessage().reply(builder.build()).queue();
	}

	protected void sendReplyEmbed(MessageReceivedEvent e, EmbedBuilder builder, long delay, TimeUnit unit, boolean footer) {
		builder.setColor(getColor(e));
		
		if (e.isFromGuild()) {
			if (footer) {
				builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()),	e.getAuthor().getEffectiveAvatarUrl());
			}
			e.getMessage().getReferencedMessage().reply(builder.build()).queue(msg -> {
				msg.delete().queueAfter(delay, unit);
			});
		} else {
			e.getMessage().getReferencedMessage().reply(builder.build()).queue();
		}
	}

	protected Message sendEmbedAndGetMessage(MessageReceivedEvent e, EmbedBuilder builder, boolean footer) {
		builder.setColor(getColor(e));
		
		if (e.isFromGuild() && footer)
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		
		return e.getChannel().sendMessage(builder.build()).complete();
	}
	
	protected void sendToOwner(MessageReceivedEvent e, EmbedBuilder builder) {
		e.getJDA().getUserById(Bootstrap.holo.getConfig().getOwnerId()).openPrivateChannel().complete().sendMessage(builder.build()).queue();
	}
	
	public String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public CommandCategory getCommandCategory() {
		return this.category;
	}

	public void setCommandCategory(CommandCategory category) {
		this.category = category;
	}

	public String getDescription() {
		return this.description;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	public String getUsage() {
		return this.usage;
	}

	protected void setUsage(String usage) {
		this.usage = usage;
	}

	public String getExample() {
		return this.example;
	}

	protected void setExample(String example) {
		this.example = example;
	}

	public List<String> getAliases() {
		return this.aliases;
	}

	protected void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	public boolean isOwnerCommand() {
		return this.isOwnerCommand;
	}

	protected void setIsOwnerCommand(boolean isOwnerCommand) {
		this.isOwnerCommand = isOwnerCommand;
	}

	public boolean isAdminCommand() {
		return this.isAdminCommand;
	}

	protected void setIsAdminCommand(boolean isAdminCommand) {
		this.isAdminCommand = isAdminCommand;
	}

	public boolean isNSFW() {
		return this.isNSFW;
	}

	protected void setIsNSFW(boolean isNSFW) {
		this.isNSFW = isNSFW;
	}

	public boolean isGuildOnlyCommand() {
		return this.isGuildOnly;
	}

	protected void setIsGuildOnlyCommand(boolean isGuildOnlyCommand) {
		this.isGuildOnly = isGuildOnlyCommand;
	}

	public boolean hasCmdCooldown() {
		return this.cooldownDuration != 0;
	}

	public int getCmdCooldown() {
		return this.cooldownDuration;
	}

	protected void setCmdCooldown(int seconds) {
		this.cooldownDuration = seconds;
	}

	protected String[] getArgs() {
		return this.args;
	}
}