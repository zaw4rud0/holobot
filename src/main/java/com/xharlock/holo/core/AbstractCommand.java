package com.xharlock.holo.core;

import com.xharlock.holo.annotations.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class representing a bot command
 */
public abstract class AbstractCommand {

	protected String[] args;

	public abstract void onCommand(MessageReceivedEvent e);

	protected String getPrefix(MessageReceivedEvent e) {
		if (e.isFromGuild()) {
			return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getPrefix();
		} else {
			return Bootstrap.holo.getConfig().getDefaultPrefix();
		}
	}

	/** Returns the default color of the bot. */
	protected Color getDefaultColor() {
		return Bootstrap.holo.getConfig().getDefaultColor();
	}

	/**
	 * Checks if the user is the owner of this bot instance.
	 */
	protected boolean isBotOwner(MessageReceivedEvent event) {
		return event.getAuthor().getIdLong() == Bootstrap.holo.getConfig().getOwnerId();
	}

	/**
	 * Checks if the member is an administrator of the guild.
	 */
	protected boolean isGuildAdmin(MessageReceivedEvent event) {
		if (event.isFromGuild() && event.getMember() != null) {
			return event.getMember().equals(event.getGuild().getOwner());
		}
		return false;
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

		// Command was called in a DM channel
		if (!e.isFromGuild()) {
			e.getChannel().sendMessageEmbeds(builder.build()).queue();
			return;
		}

		if (footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(delay, unit));
	}

	protected void sendEmbed(MessageReceivedEvent e, EmbedBuilder builder, long delay, TimeUnit unit, boolean footer, Color color) {
		builder.setColor(color);

		// Command was called in a DM channel
		if (!e.isFromGuild()) {
			e.getChannel().sendMessageEmbeds(builder.build()).queue();
			return;
		}

		if (footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(delay, unit));
	}

	@Deprecated
	protected void sendReplyEmbed(MessageReceivedEvent e, Message reply, EmbedBuilder builder, boolean footer) {
		builder.setColor(getDefaultColor());
		if (e.isFromGuild() && footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		reply.replyEmbeds(builder.build()).queue();
	}

	protected void sendReplyEmbed(MessageReceivedEvent e, Message reply, EmbedBuilder builder, long delay, TimeUnit unit, boolean footer) {
		builder.setColor(getDefaultColor());

		// Command was called in a DM channel
		if (!e.isFromGuild()) {
			reply.replyEmbeds(builder.build()).queue();
		}

		if (footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		reply.replyEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(delay, unit));
	}

	protected Message sendEmbedAndGetMessage(MessageReceivedEvent e, EmbedBuilder builder, boolean footer) {
		builder.setColor(getDefaultColor());
		if (e.isFromGuild() && footer) {
			builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		}
		return e.getChannel().sendMessageEmbeds(builder.build()).complete();
	}

	/**
	 * Sends an embed to the owner of the bot.
	 */
	protected void sendToOwner(MessageReceivedEvent e, EmbedBuilder builder) {
		User owner = e.getJDA().getUserById(Bootstrap.holo.getConfig().getOwnerId());
		if (owner != null) {
			owner.openPrivateChannel().complete().sendMessageEmbeds(builder.build()).queue();
		}
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

	/**
	 * Checks whether a String is an integer.
	 */
	protected boolean isInteger(String s) {
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

	/**
	 * Checks whether a String is a long.
	 */
	protected boolean isLong(String s) {
		if (s == null) {
			return false;
		}
		try {
			Long.parseLong(s);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether a given URL is valid.
	 */
	protected boolean isValidURL(String url) {
		UrlValidator urlValidator = new UrlValidator();
		return urlValidator.isValid(url);
	}

	/**
	 * Checks if a message has pings or mentions in it
	 */
	public boolean hasPings(Message msg) {
		return !msg.getMentionedMembers().isEmpty() || !msg.getMentionedRoles().isEmpty() || !msg.getMentionedUsers().isEmpty();
	}

	/**
	 * Returns the image link (if there is any) from a message. If no image could be found, it returns null.
	 */
	@Nullable
	public String getImage(Message msg) {
		// Image as attachment
		List<Message.Attachment> attachments = msg.getAttachments();
		String url = !attachments.isEmpty() ? attachments.get(0).getUrl() : null;
		if (url != null) {
			return url;
		}

		// Image in an embed
		MessageEmbed.ImageInfo image = msg.getEmbeds().isEmpty() ? null : msg.getEmbeds().get(0).getImage();
		if (image != null) {
			return image.getUrl();
		}

		// Image url as message text
		url = msg.getContentRaw().split(" ")[1].replace("<", "").replace(">", "");
		if (isValidURL(url)) {
			return url;
		}

		// No image found
		else {
			return null;
		}
	}

	/**
	 * Returns the name of the command.
	 */
	public String getName() {
		return getClass().getAnnotation(Command.class).name();
	}

	/**
	 * Returns the description of the command.
	 */
	public String getDescription() {
		return getClass().getAnnotation(Command.class).description();
	}

	/**
	 * Returns the usage of the command.
	 */
	@Nullable
	public String getUsage() {
		String usage = getClass().getAnnotation(Command.class).usage();
		if (usage.isEmpty()) {
			return null;
		}
		return usage;
	}

	/**
	 * Returns an example of the command.
	 */
	@Nullable
	public String getExample() {
		String example = getClass().getAnnotation(Command.class).example();
		if (example.isEmpty()) {
			return null;
		}
		return example;
	}

	/**
	 * Returns the aliases of the command.
	 */
	public String[] getAlias() {
		return getClass().getAnnotation(Command.class).alias();
	}

	/**
	 * Returns the thumbnail of the command.
	 */
	@Nullable
	public String getThumbnail() {
		String thumbnail = getClass().getAnnotation(Command.class).thumbnail();
		if (thumbnail.isEmpty()) {
			return null;
		}
		return thumbnail;
	}

	/**
	 * Returns the embed color of the command.
	 */
	public Color getEmbedColor() {
		return getClass().getAnnotation(Command.class).embedColor().getColor();
	}

	/**
	 * Returns the command category.
	 */
	public CommandCategory getCategory() {
		return getClass().getAnnotation(Command.class).category();
	}

	/**
	 * Returns whether this command can only be used in a guild.
	 */
	public boolean isGuildOnly() {
		return getClass().getAnnotation(Command.class).guildOnly();
	}

	/**
	 * Returns whether this command can only be used by guild administrators.
	 */
	public boolean isAdminOnly() {
		return getClass().getAnnotation(Command.class).adminOnly();
	}

	/**
	 * Returns whether this command can only be used by the bot owner.
	 */
	public boolean isOwnerOnly() {
		return getClass().getAnnotation(Command.class).ownerOnly();
	}

	/**
	 * Returns whether this command is NSFW (not safe for work).
	 */
	public boolean isNSFW() {
		return getClass().getAnnotation(Command.class).isNSFW();
	}
}