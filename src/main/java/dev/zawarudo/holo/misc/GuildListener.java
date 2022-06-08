package dev.zawarudo.holo.misc;

import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.music.core.GuildMusicManager;
import dev.zawarudo.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * Class that listens to changes inside a guild or any guild-related events
 */
public class GuildListener extends ListenerAdapter {

    /**
     * Event that is fired when this bot instance joins a guild
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent e) {
        try {
            // Store members
            DBOperations.insertMembers(e.getGuild().getMembers());
            // Store guild information
            DBOperations.insertGuild(e.getGuild());
            // Store emotes of the guild
            DBOperations.insertEmotes(e.getGuild().getEmotes());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event that is fired when a new user joins a guild
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        try {
            DBOperations.insertMember(e.getMember());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /** Event that is fired when a member leaves, is banned or is kicked from a guild */
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent e) {
        try {
            if (e.getMember() != null) {
                DBOperations.deleteMember(e.getMember());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event that is fired when a member changes their nickname
     */
    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent e) {
        try {
            DBOperations.insertNickname(e.getMember());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event that is fired when a new emote is added to the guild
     */
    @Override
    public void onEmoteAdded(@NotNull EmoteAddedEvent e) {
        try {
            DBOperations.insertEmote(e.getEmote());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event that is fired when the owner of a guild changes
     */
    @Override
    public void onGuildUpdateOwner(@NotNull GuildUpdateOwnerEvent e) {
        try {
            DBOperations.updateGuild(e.getGuild());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event that is fired when a guild chances its name
     */
    @Override
    public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent e) {
        try {
            DBOperations.updateGuild(e.getGuild());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event that is fired when a member leaves a voice channel
     */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        // Holo's voice state has been disabled
        if (e.getGuild().getSelfMember().getVoiceState() == null) {
            return;
        }

        // Holo is not in any voice channel, thus this event is irrelevant
        if (!e.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            return;
        }

        AudioChannel botVoice = e.getGuild().getSelfMember().getVoiceState().getChannel();

        // Nobody except Holo is in the voice channel
        if (e.getChannelLeft().equals(botVoice) && botVoice.getMembers().size() <= 1) {
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
            musicManager.clear();
            e.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    /**
     * Event that is fired when a user changes their username
     */
    @Override
    public void onUserUpdateName(@Nonnull UserUpdateNameEvent e) {
        try {
            DBOperations.updateUser(e.getUser());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event that is fired when a user changes their discriminator
     */
    @Override
    public void onUserUpdateDiscriminator(@Nonnull UserUpdateDiscriminatorEvent e) {
        try {
            DBOperations.updateUser(e.getUser());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}