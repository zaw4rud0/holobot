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

	protected boolean isBotInChannel(MessageReceivedEvent e) {
		return e.getGuild().getSelfMember().getVoiceState().inVoiceChannel();
	}
	
	protected boolean isUserInChannel(MessageReceivedEvent e) {
		return e.getMember().getVoiceState().inVoiceChannel();
	}

	protected boolean isUserInSameChannel(MessageReceivedEvent e) {
		if (!e.getMember().getVoiceState().inVoiceChannel() || !e.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return false;
		else
			return e.getGuild().getSelfMember().getVoiceState().getChannel()
					.equals(e.getMember().getVoiceState().getChannel());
	}
}
