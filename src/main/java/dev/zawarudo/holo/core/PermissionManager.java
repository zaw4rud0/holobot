package dev.zawarudo.holo.core;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.database.dao.BlacklistedDao;
import dev.zawarudo.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles the permissions and checks if a user is allowed to use a command.
 */
public class PermissionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionManager.class);

    private final BlacklistedDao blacklistedDao;
    private final Set<Long> blacklistedIds = ConcurrentHashMap.newKeySet();

    public PermissionManager(BlacklistedDao blacklistedDao) {
        this.blacklistedDao = blacklistedDao;

        try {
            blacklistedIds.addAll(blacklistedDao.findAllUserIds());
        } catch (SQLException ex) {
            LOGGER.error("Failed to load blacklist", ex);
        }
    }

    public boolean hasChannelPermission(MessageReceivedEvent event, AbstractCommand cmd) {
        return isCommandAllowedInChannelType(event, cmd) && checkIsNSFWCommandAllowed(event, cmd);
    }

    private boolean isCommandAllowedInChannelType(MessageReceivedEvent event, AbstractCommand cmd) {
        return !event.isFromType(ChannelType.PRIVATE) || !cmd.isGuildOnly();
    }

    private boolean checkIsNSFWCommandAllowed(MessageReceivedEvent event, AbstractCommand cmd) {
        // Users can use NSFW commands in DMs without restrictions
        if (event.isFromType(ChannelType.PRIVATE)) {
            return true;
        }

        if (!cmd.isNSFW()) {
            return true;
        }

        GuildConfig config = getGuildConfig(event.getGuild());

        if (!isNSFWConfigEnabled(event.getGuild())) {
            sendErrorEmbed(
                    event,
                    "NSFW commands are disabled in this server.\n" +
                            "You can enable them via " +
                            Formatter.asCodeBlock(config.getPrefix() + "config nsfw true")
            );
            return false;
        }

        if (!isChannelNSFW(event.getChannel())) {
            sendErrorEmbed(
                    event,
                    "You can't use NSFW commands outside NSFW channels.\n" +
                            "Please move to a NSFW channel to use this command."
            );
            return false;
        }

        return true;
    }

    private boolean isNSFWConfigEnabled(Guild guild) {
        return getGuildConfig(guild).isNSFWEnabled();
    }

    private boolean isChannelNSFW(@NotNull MessageChannelUnion channel) {
        // Thread channel inherits settings from parent channel
        if (channel.getType().isThread()) {
            ThreadChannel thread = channel.asThreadChannel();
            return thread.getParentChannel().asTextChannel().isNSFW();
        }
        return channel instanceof IAgeRestrictedChannel c && c.isNSFW();
    }

    /**
     * Checks if a user is allowed to use a command.
     *
     * @param event The event that triggered the command.
     * @param cmd   The command that was called.
     * @return True if the user is allowed to use the command, false otherwise.
     */
    public boolean hasUserPermission(@NotNull MessageReceivedEvent event, @NotNull AbstractCommand cmd) {
        // Bot owner can use all commands
        if (cmd.isBotOwner(event.getAuthor())) {
            return true;
        }

        // Guild owners can use all admin commands
        else if (cmd.isAdminOnly() && cmd.isGuildAdmin(event)) {
            return true;
        }

        // Normal users can use all non-admin and non-owner commands
        else if (!cmd.isOwnerOnly() && !cmd.isAdminOnly()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a user is blacklisted from using the bot.
     */
    public boolean isBlacklisted(@NotNull User user) {
        return blacklistedIds.contains(user.getIdLong());
    }

    /**
     * Blacklists a user from using the bot.
     */
    public void blacklist(long userId, @NotNull String reason, @NotNull String date) throws SQLException {
        var entry = new BlacklistedDao.Blacklisted(userId, reason, date);
        blacklistedDao.insertIgnore(entry);
        blacklistedIds.add(userId);
    }

    /**
     * Removes a user from the blacklist.
     */
    public void unblacklist(long userId) throws SQLException {
        blacklistedDao.deleteByUserId(userId);
        blacklistedIds.remove(userId);
    }

    private GuildConfig getGuildConfig(Guild guild) {
        return Bootstrap.holo.getGuildConfigManager().getGuildConfig(guild);
    }

    private void sendErrorEmbed(MessageReceivedEvent event, String message) {
        event.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Error")
                .setDescription(message)
                .setColor(Color.RED);
        event.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
    }
}