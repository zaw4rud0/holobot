package dev.zawarudo.holo.core;

import dev.zawarudo.holo.database.DBOperations;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the permissions and checks if a user is allowed to use a command.
 */
public class PermissionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionManager.class);

    private List<Long> blacklisted;

    public PermissionManager() {
        blacklisted = new ArrayList<>();

        try {
            blacklisted = DBOperations.getBlacklistedUsers();
        } catch (SQLException ex) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Something went wrong while fetching the blocked users", ex);
            }
        }
    }

    /**
     * Checks if a user is allowed to use a command in the channel.
     *
     * @param event The event that triggered the command.
     * @param cmd   The command that was called.
     * @return True if the user is allowed to use the command, false otherwise.
     */
    public boolean hasChannelPermission(@NotNull MessageReceivedEvent event, @NotNull AbstractCommand cmd) {
        boolean hasPermission = true;

        // Guild commands can't be used in DMs
        if (event.isFromType(ChannelType.PRIVATE) && cmd.isGuildOnly()) {
            hasPermission = false;
        }

        // NSFW commands can't be used in non-NSFW channels
        if (cmd.isNSFW() && !isNsfwAllowed(event.getChannel())) {
            hasPermission = false;
        }

        return hasPermission;
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
     * Checks if a channel allows NSFW commands.
     *
     * @param channel The {@link MessageChannelUnion} to check.
     * @return True if the channel allows NSFW commands, false otherwise.
     */
    public boolean isNsfwAllowed(@NotNull MessageChannelUnion channel) {
        // Thread channel
        if (channel.getType().isThread()) {
            ThreadChannel thread = channel.asThreadChannel();
            return thread.getParentChannel().asTextChannel().isNSFW();
        }

        // Text channel
        if (channel.getType() == ChannelType.TEXT) {
            return channel.asTextChannel().isNSFW();
        }

        // Voice channel
        if (channel.getType() == ChannelType.VOICE) {
            return channel.asVoiceChannel().isNSFW();
        }

        // News channel
        if (channel.getType() == ChannelType.NEWS) {
            return channel.asNewsChannel().isNSFW();
        }

        // Private channel
        return false;
    }

    /**
     * Checks if a user is blacklisted from using the bot.
     *
     * @param user The {@link User} to check.
     * @return True if the user is blacklisted, false otherwise.
     */
    public boolean isBlacklisted(@NotNull User user) {
        return blacklisted.contains(user.getIdLong());
    }

    /**
     * Blacklists a user from using the bot.
     *
     * @param user   The {@link User} to blacklist.
     * @param reason The reason for blacklisting the user.
     * @param date   The date when the user was blacklisted.
     * @throws SQLException If the database operation fails.
     */
    public void blacklist(@NotNull User user, @NotNull String reason, @NotNull String date) throws SQLException {
        blacklisted.add(user.getIdLong());
        DBOperations.insertBlacklistedUser(user.getIdLong(), date, reason);
    }
}