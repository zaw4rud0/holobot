package com.xharlock.holo.music.core;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class MusicCommand extends Command {

	public MusicCommand(String name) {
		super(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.MUSIC);
	}

	/**
	 * Checks if the bot is in a voice channel of the guild
	 */
	protected boolean isBotInAudioChannel(MessageReceivedEvent e) {
		return e.getGuild().getSelfMember().getVoiceState().inAudioChannel();
	}
	
	/**
	 * Checks if the user who invoked the command is in a voice channel
	 */
	protected boolean isUserInAudioChannel(MessageReceivedEvent e) {
		return e.getMember().getVoiceState().inAudioChannel();
	}

	/**
	 * Checks if the user and the bot are in the same voice channel
	 */
	protected boolean isUserInSameAudioChannel(MessageReceivedEvent e) {
		if (!isUserInAudioChannel(e) || !isBotInAudioChannel(e)) {
			return false;
		} else {
			return e.getGuild().getSelfMember().getVoiceState().getChannel().equals(e.getMember().getVoiceState().getChannel());
		}
	}
}