package com.xharlock.holo.misc;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Class that listens to changes inside a guild or any guild-related events
 */
public class GuildListener extends ListenerAdapter {
	
	// When Holo joins a new guild
	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event) {
		
	}
	
	// When a new user joins a guild
	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		
	}
	
	// When a guild member changes their nickname
	@Override
	public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent event) {
		
	}

	// When a new emote is added to a guild
	@Override
	public void onEmoteAdded(@Nonnull EmoteAddedEvent event) {
		
	}
	
	// When the owner of a guild changes
	@Override
	public void onGuildUpdateOwner(@Nonnull GuildUpdateOwnerEvent event) {
		
	}
	
}
