package com.xharlock.otakusenpai.music.core;

import com.xharlock.otakusenpai.commands.core.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class MusicCommand extends Command {

	public MusicCommand(String name) {
		super(name);
	}

	public boolean isBotInChannel(MessageReceivedEvent e) {
		return e.getGuild().getSelfMember().getVoiceState().inVoiceChannel();
	}

	public boolean isUserInSameChannel(MessageReceivedEvent e) {
		if (!e.getMember().getVoiceState().inVoiceChannel())
			return false;
		else
			return e.getGuild().getSelfMember().getVoiceState().getChannel()
					.equals(e.getMember().getVoiceState().getChannel());
	}
}
