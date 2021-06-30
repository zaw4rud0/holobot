package com.xharlock.holo.music.core;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceChannelListener extends ListenerAdapter {
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		System.out.println("Someone joined a voice channel!");
		
		// Check if it's the same channel as the bot
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		System.out.println("Someone left the voice channel!");
		
		// Check if it's the same channel as the bot
	}
	
}
