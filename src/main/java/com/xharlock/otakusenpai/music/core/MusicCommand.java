package com.xharlock.otakusenpai.music.core;

import java.net.URL;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class MusicCommand extends Command {

	public MusicCommand(String name) {
		super(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.MUSIC);
	}

	public boolean isBotInChannel(MessageReceivedEvent e) {
		return e.getGuild().getSelfMember().getVoiceState().inVoiceChannel();
	}
	
	public boolean isUserInChannel(MessageReceivedEvent e) {
		return e.getMember().getVoiceState().inVoiceChannel();
	}

	public boolean isUserInSameChannel(MessageReceivedEvent e) {
		if (!e.getMember().getVoiceState().inVoiceChannel() || !e.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
			return false;
		else
			return e.getGuild().getSelfMember().getVoiceState().getChannel()
					.equals(e.getMember().getVoiceState().getChannel());
	}
	
	public boolean isValidUrl(String url) {
        try {
            new URL(url).openStream().close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
