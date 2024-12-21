package dev.zawarudo.holo.core.misc;

import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.GuildConfig;
import dev.zawarudo.holo.core.GuildConfigManager;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.commands.music.GuildMusicManager;
import dev.zawarudo.holo.commands.music.PlayerManager;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * Class that listens to changes inside a guild or any guild-related events.
 */
public class GuildListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildListener.class);

    private final GuildConfigManager manager;

    public GuildListener(GuildConfigManager manager) {
        this.manager = manager;
    }

    /**
     * Event that is fired when this bot instance joins a guild.
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        try {
            logInfo("Joined a new server ({}). Saving members and emotes...", event.getGuild().getId());

            DBOperations.insertMembers(event.getGuild().getMembers());
            DBOperations.insertGuild(event.getGuild());

            Bootstrap.holo.getEmoteManager().insertEmotes(event.getGuild().getEmojis().stream()
                    .map(e -> (CustomEmoji) e)
                    .toArray(CustomEmoji[]::new)
            );

            // Create new guild configuration and save it in the DB
            GuildConfig config = manager.getGuildConfig(event.getGuild());
            DBOperations.insertGuildConfig(config);

            logInfo("Saving successful for guild ({})", event.getGuild());
        } catch (SQLException ex) {
            logError("Something went wrong while storing the server in the DB.", ex);
        }
    }

    /**
     * Event that is fired when this bot instance leaves a guild.
     */
    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        try {
            logInfo("Left the server ({}). Removing members and config from the DB...", event.getGuild().getId());

            DBOperations.deleteMembers(event.getGuild().getMembers());
            DBOperations.deleteGuild(event.getGuild());

            DBOperations.deleteGuildConfig(manager.getGuildConfig(event.getGuild()));

            logInfo("Successful removed guild ({}) from the database.", event.getGuild());
        } catch (SQLException ex) {
            logError("Something went wrong while removing the server from the DB.", ex);
        }
    }

    /**
     * Event that is fired when a new user joins a guild.
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        try {
            DBOperations.insertMember(event.getMember());
        } catch (SQLException ex) {
            logError("Something went wrong while storing a new member in the DB.", ex);
        }
    }

    /**
     * Event that is fired when a member leaves, is banned or is kicked from a guild.
     */
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        try {
            if (event.getMember() != null) {
                DBOperations.deleteMember(event.getMember());
            }
        } catch (SQLException ex) {
            logError("Something went wrong while removing the guild member from the DB.", ex);
        }
    }

    /**
     * Event that is fired when a member changes their nickname.
     */
    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        try {
            DBOperations.insertNickname(event.getMember());
        } catch (SQLException ex) {
            logError("Something went wrong while storing the new nickname of a member in the DB.", ex);
        }
    }

    /**
     * Event that is fired when a new emote is added to the guild.
     */
    @Override
    public void onEmojiAdded(@NotNull EmojiAddedEvent event) {
        try {
            Bootstrap.holo.getEmoteManager().insertEmotes(event.getEmoji());
        } catch (SQLException ex) {
            logError("Something went wrong while storing the new emoji in the DB.", ex);
        }
    }

    /**
     * Event that is fired when a guild chances its name.
     */
    @Override
    public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent event) {
        try {
            DBOperations.updateGuild(event.getGuild());
        } catch (SQLException ex) {
            logError("Something went wrong while storing the new guild name in the DB.", ex);
        }
    }

    /**
     * Event that is fired when a member leaves a voice channel.
     */
    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        // Holo's voice state has been disabled
        if (event.getGuild().getSelfMember().getVoiceState() == null) {
            return;
        }

        // Holo is not in any voice channel, thus this event is irrelevant
        if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            return;
        }

        if (event.getChannelLeft() == null) {
            return;
        }

        AudioChannelUnion botVoice = event.getGuild().getSelfMember().getVoiceState().getChannel();

        if (event.getChannelLeft().equals(botVoice) && botVoice.getMembers().size() <= 1) {
            logInfo("No one is in the voice channel anymore. Leaving it...");

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            musicManager.clear();
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    /**
     * Event that is fired when a user changes their username.
     */
    @Override
    public void onUserUpdateName(@Nonnull UserUpdateNameEvent event) {
        try {
            DBOperations.updateUser(event.getUser());
        } catch (SQLException ex) {
            logError("Something went wrong while the new username in the DB.", ex);
        }
    }

    private void logInfo(String msg, Object... args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(msg, args);
        }
    }

    private void logError(String msg, Throwable t) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(msg, t);
        }
    }
}