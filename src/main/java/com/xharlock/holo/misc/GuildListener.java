package com.xharlock.holo.misc;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

	/**
	 * Event that fires when this bot instance joins a guild<br>
	 * 
	 * TODO: Use this to set up the guild configuration of the bot
	 */
	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent e) {
		Guild guild = e.getGuild();
		System.out.println(guild.getName());
	}

	/**
	 * Event that fires when a new user joins a guild<br>
	 * 
	 * TODO: Use this to greet a person, add them to the DB, etc.
	 */
	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent e) {
		Member member = e.getMember();
		System.out.println(member.getEffectiveName());
	}

	/**
	 * Event that fires when a member changes their nickname<br>
	 * 
	 * TODO: Use this to add the new nickname to the DB. This way Holo can keep
	 * track of the nickname history of an user and display it in <whois
	 */
	@Override
	public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent e) {
		Member member = e.getMember();
		System.out.println(String.format("%s changed their nickname from %s to %s", member.getEffectiveName(), e.getOldNickname(), e.getNewNickname()));
	}

	/**
	 * Event that fires when a new emote is added to the guild
	 */
	@Override
	public void onEmoteAdded(@Nonnull EmoteAddedEvent e) {
		System.out.println(e.getEmote().getAsMention());
	}

	/**
	 * Event that fires when the owner of a guild changes<br>
	 * 
	 * TODO: Use this to change the owner in the guild configuration of the bot
	 */
	@Override
	public void onGuildUpdateOwner(@Nonnull GuildUpdateOwnerEvent event) {
		System.out.println(String.format("Guild owner changed from %s to %s", event.getOldOwner().getEffectiveName(), event.getNewOwner().getEffectiveName()));
	}

}
