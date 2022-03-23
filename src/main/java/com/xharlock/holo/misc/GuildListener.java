package com.xharlock.holo.misc;

import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.PlayerManager;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Class that listens to changes inside a guild or any guild-related events
 */
public class GuildListener extends ListenerAdapter {

	/**
	 * Event that is fired when this bot instance joins a guild<br>
	 * 
	 * TODO: Use this to set up the guild configuration of the bot
	 */
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
	}

	/**
	 * Event that is fired when a new user joins a guild<br>
	 * 
	 * TODO: Use this to greet a person, add them to the DB, etc.
	 */
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
	}

	/**
	 * Event that is fired when a member changes their nickname<br>
	 * 
	 * TODO: Use this to add the new nickname to the DB. This way Holo can keep
	 * track of the nickname history of an user and display it in <whois
	 */
	@Override
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent e) {
	}

	/**
	 * Event that is fired when a new emote is added to the guild
	 */
	@Override
	public void onEmoteAdded(EmoteAddedEvent e) {
	}

	/**
	 * Event that is fired when the owner of a guild changes<br>
	 * 
	 * TODO: Use this to change the owner in the guild configuration of the bot
	 */
	@Override
	public void onGuildUpdateOwner(GuildUpdateOwnerEvent e) {
	}

	/**
	 * Event that is fired when a member joins a voice channel
	 */
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
	}

	/**
	 * Event that is fired when a member leaves a voice channel
	 */
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {		
		// Holo is not in any voice channel, thus this event is irrelevant
		if (!e.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
			return;
		}
		
		AudioChannel botVoice = e.getGuild().getSelfMember().getVoiceState().getChannel();
		
		// Member left voice channel of the bot
		if (e.getChannelLeft().equals(botVoice)) {
			if (botVoice.getMembers().size() <= 1) {
				GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
				musicManager.clear();
				e.getGuild().getAudioManager().closeAudioConnection();
			}
		}
	}
}