package dev.zawarudo.holo.music.core;

import dev.zawarudo.holo.core.AbstractCommand;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMusicCommand extends AbstractCommand {

	/**
	 * Returns the voice state of the bot within a guild.
	 */
    @Nullable
    protected GuildVoiceState getSelfVoiceState(Guild guild) {
        return guild.getSelfMember().getVoiceState();
    }

    /**
     * Checks if the bot is in a voice channel of the guild
     */
    protected boolean isBotInAudioChannel(MessageReceivedEvent e) {
        GuildVoiceState selfVoiceState = getSelfVoiceState(e.getGuild());
        if (selfVoiceState == null) {
            return false;
        }
        return selfVoiceState.inAudioChannel();
    }

    /**
     * Checks if the user who invoked the command is in a voice channel
     */
    protected boolean isUserInAudioChannel(Member member) {
        if (member.getVoiceState() == null) {
            return false;
        }
        return member.getVoiceState().inAudioChannel();
    }

    /**
     * Checks if the user and the bot are in the same voice channel
     */
    protected boolean isUserInSameAudioChannel(MessageReceivedEvent e) {
        if (e.getMember() == null) {
            return false;
        }
        if (!isUserInAudioChannel(e.getMember()) || !isBotInAudioChannel(e)) {
            return false;
        }

		// Check voice states
		GuildVoiceState selfVoiceState = getSelfVoiceState(e.getGuild());
		GuildVoiceState memberVoiceState = e.getMember().getVoiceState();
        if (selfVoiceState == null || memberVoiceState == null) {
            return false;
        }

        AudioChannel botChannel = selfVoiceState.getChannel();
        if (botChannel == null) {
            return false;
        }
        AudioChannel userChannel = memberVoiceState.getChannel();
        if (userChannel == null) {
            return false;
        }
        return botChannel.equals(e.getMember().getVoiceState().getChannel());
    }
}