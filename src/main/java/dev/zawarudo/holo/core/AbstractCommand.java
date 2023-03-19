package dev.zawarudo.holo.core;

import dev.zawarudo.holo.annotations.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.entities.Message.Attachment;

/**
 * Abstract class representing a bot command.
 */
public abstract class AbstractCommand {

    protected String[] args;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Abstract method that defines the function of the command.
     *
     * @param event The {@link MessageReceivedEvent} to trigger the command with.
     */
    public abstract void onCommand(@NotNull MessageReceivedEvent event);

    /**
     * Returns the prefix of the bot in the guild. If the guild didn't
     * set a custom prefix, the default prefix will be returned.
     *
     * @return The prefix of the bot needed to invoke commands.
     */
    protected String getPrefix(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            return Bootstrap.holo.getGuildConfigManager().getGuildConfig(event.getGuild()).getPrefix();
        } else {
            return Bootstrap.holo.getConfig().getDefaultPrefix();
        }
    }

    /**
     * Checks if a given {@link User} is the owner of this bot.
     *
     * @param user The {@link User} to check.
     * @return True if the user is the owner of the guild, false otherwise.
     */
    public boolean isBotOwner(User user) {
        return user.getIdLong() == Bootstrap.holo.getConfig().getOwnerId();
    }

    /**
     * Checks if the user is an administrator of the guild.
     *
     * @param event The {@link MessageReceivedEvent} to check with.
     * @return True if the user is an administrator of the guild, false otherwise.
     */
    protected boolean isGuildAdmin(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild() && event.getMember() != null) {
            return event.getMember().equals(event.getGuild().getOwner());
        }
        return false;
    }

    /**
     * Deletes the message that triggered the command if and only if the message is from a guild. Messages
     * from direct channels can't be deleted by the bot.
     *
     * @param event The {@link MessageReceivedEvent} to delete the message from.
     */
    protected void deleteInvoke(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            event.getMessage().delete().queue();
        }
    }

    /**
     * Sends a typing notification in the channel the command was invoked in. In other words, the user will
     * see <code>Holo is typing...</code> at the bottom.
     *
     * @param event The {@link MessageReceivedEvent} to send the typing notification in.
     */
    protected void sendTyping(@NotNull MessageReceivedEvent event) {
        event.getChannel().sendTyping().queue();
    }

    /**
     * Sends an embed to the channel the command was invoked in.
     */
    protected void sendEmbed(MessageReceivedEvent event, EmbedBuilder embedBuilder, boolean footer) {
        sendEmbed(event, embedBuilder, footer, null);
    }

    /**
     * Sends an embed to the channel the command was invoked in.
     */
    protected void sendEmbed(MessageReceivedEvent event, EmbedBuilder embedBuilder, boolean footer, Color embedColor) {
        if (footer) {
            addFooter(event, embedBuilder);
        }
        embedBuilder.setColor(embedColor);
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Sends an embed to the channel the command was invoked in and deletes it after a given amount of time.
     */
    protected void sendEmbed(MessageReceivedEvent event, EmbedBuilder embedBuilder, boolean footer, long delay, TimeUnit unit) {
        sendEmbed(event, embedBuilder, footer, delay, unit, null);
    }

    /**
     * Sends an embed to the channel the command was invoked in and deletes it after a given amount of time.
     */
    protected void sendEmbed(MessageReceivedEvent event, EmbedBuilder embedBuilder, boolean footer, long delay, TimeUnit unit, Color embedColor) {
        if (footer) {
            addFooter(event, embedBuilder);
        }
        embedBuilder.setColor(embedColor);
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue(msg -> msg.delete().queueAfter(delay, unit));
    }

    /**
     * Sends an embed stating that an error occurred with some information.
     */
    protected void sendErrorEmbed(MessageReceivedEvent event, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Error");
        builder.setDescription(message);
        sendEmbed(event, builder, false, 30, TimeUnit.SECONDS, Color.RED);
    }

    /**
     * Sends an embed to the owner of the bot.
     */
    public void sendToOwner(EmbedBuilder builder) {
        User owner = Bootstrap.holo.getJDA().getUserById(Bootstrap.holo.getConfig().getOwnerId());
        if (owner == null) {
            if (logger.isErrorEnabled()) {
                logger.error("Owner is null which wasn't supposed to happen! Please check your config!");
            }
            return;
        }
        owner.openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(builder.build()).queue());
    }

    /**
     * Adds a footer to the embed if and only if the event is from a guild.
     */
    private void addFooter(@NotNull MessageReceivedEvent event, @NotNull EmbedBuilder builder) {
        if (event.getMember() == null) {
            return;
        }
        String footerText = String.format("Invoked by %s", event.getMember().getEffectiveName());
        builder.setFooter(footerText, event.getAuthor().getAvatarUrl());
    }

    /**
     * Checks whether a String is an integer.
     *
     * @param s The String to check.
     * @return True if the String is an integer, false otherwise.
     */
    protected boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether a String is a long.
     *
     * @param s The String to check.
     * @return True if the String is a long, false otherwise.
     */
    protected boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Checks whether a given URL is valid.
     *
     * @param url The URL to check.
     * @return True if the URL is valid, false otherwise.
     */
    protected boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }

    /**
     * Checks if a message has pings or mentions in it.
     *
     * @param msg The message to check.
     * @return True if the message has pings or mentions, false otherwise.
     */
    public boolean hasPings(@NotNull Message msg) {
        Mentions mentions = msg.getMentions();
        return !mentions.getMembers().isEmpty() || !mentions.getRoles().isEmpty() || !mentions.getUsers().isEmpty();
    }

    /**
     * Returns the image link from a given message. If no image could be found,
     * null will be returned.
     *
     * @param msg The message to get the image from.
     * @return The image link from the message, or null if no image could be found.
     */
    @Nullable
    protected String getImage(@NotNull Message msg) {
        // Image as attachment
        List<Attachment> attachments = msg.getAttachments();
        if (!attachments.isEmpty()) {
            for (Attachment attachment : attachments) {
                if (attachment.isImage()) {
                    return attachment.getUrl();
                }
            }
        }
        // Image in an embed
        List<MessageEmbed> embeds = msg.getEmbeds();
        if (!embeds.isEmpty()) {
            for (MessageEmbed embed : embeds) {
                MessageEmbed.ImageInfo image = embed.getImage();
                if (image != null) {
                    return image.getUrl();
                }
            }
        }
        // Image url as message text
        String[] args = msg.getContentRaw().split(" ");
        String content = args[args.length - 1];
        if (isValidUrl(content)) {
            return content;
        }
        // No image found
        return null;
    }

    /**
     * Method to get an embed with multiple images (up to four images). This method works because embeds with the same
     * settings will be merged so that their images are displayed in one embed.
     *
     * @param builder The {@link EmbedBuilder} with the settings the final embed should have.
     * @param images  A list of image links that should be added to the embed. Note that the list can only contain up
     *                to four images. At least one image is required.
     * @return A list of {@link MessageEmbed} that will be merged to one final embed with multiple images. The list
     * should be passed to the {@link MessageChannelUnion#sendMessageEmbeds(Collection)} method that will then
     * automatically merge the embeds.
     */
    protected List<MessageEmbed> getEmbedWithMultipleImages(@NotNull EmbedBuilder builder, @NotNull List<String> images) {
        if (images.size() > 4) {
            throw new IllegalArgumentException("A single embed can only display four images! Given: " + images.size());
        }
        if (images.isEmpty()) {
            throw new IllegalArgumentException("This method requires at least one image!");
        }
        List<MessageEmbed> embeds = new ArrayList<>();
        for (String image : images) {
            builder.setImage(image);
            embeds.add(builder.build());
        }
        return embeds;
    }

    /**
     * Returns the name of the command.
     */
    @NotNull
    public String getName() {
        return getClass().getAnnotation(Command.class).name();
    }

    /**
     * Returns the description of the command.
     */
    @NotNull
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
     * Returns whether the command has a specific usage.
     */
    public boolean hasUsage() {
        return getUsage() != null;
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
     * Returns whether the command has an example.
     */
    public boolean hasExample() {
        return getExample() != null;
    }

    /**
     * Returns the aliases of the command.
     */
    @NotNull
    public String[] getAlias() {
        return getClass().getAnnotation(Command.class).alias();
    }

    /**
     * Returns whether the command has aliases.
     */
    public boolean hasAlias() {
        return getAlias().length > 0;
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
     * Returns whether the command has a thumbnail.
     */
    public boolean hasThumbnail() {
        return getThumbnail() != null;
    }

    /**
     * Returns the embed color of the command.
     */
    @Nullable
    public Color getEmbedColor() {
        return getClass().getAnnotation(Command.class).embedColor().getColor();
    }

    /**
     * Returns the command category.
     */
    @NotNull
    public CommandCategory getCategory() {
        return getClass().getAnnotation(Command.class).category();
    }

    /**
     * Checks whether this command is available for everyone.
     */
    public boolean isPublic() {
        return !isAdminOnly() && !isOwnerOnly();
    }

    /**
     * Checks whether this command can only be used in a guild.
     */
    public boolean isGuildOnly() {
        return getClass().getAnnotation(Command.class).guildOnly();
    }

    /**
     * Checks whether this command can only be used by guild administrators.
     */
    public boolean isAdminOnly() {
        return getClass().getAnnotation(Command.class).adminOnly();
    }

    /**
     * Checks whether this command can only be used by the bot owner.
     */
    public boolean isOwnerOnly() {
        return getClass().getAnnotation(Command.class).ownerOnly();
    }

    /**
     * Checks whether this command is NSFW (not safe for work).
     */
    public boolean isNSFW() {
        return getClass().getAnnotation(Command.class).isNSFW();
    }
}