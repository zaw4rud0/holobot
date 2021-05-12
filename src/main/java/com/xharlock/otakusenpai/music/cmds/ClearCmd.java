package com.xharlock.otakusenpai.music.cmds;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.music.core.*;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearCmd extends MusicCommand {

	private EventWaiter waiter;
	private HashMap<Guild, Boolean> voting;
	
	public ClearCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to clear the queue");
		setUsage(name);
		this.waiter = waiter;
		this.voting = new HashMap<>();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		
		EmbedBuilder builder = new EmbedBuilder();
		
		if (musicManager.scheduler.queue.isEmpty()) {
            builder.setTitle("Error");
            builder.setDescription("My queue is already empty");
            sendEmbed(e, builder, 15L, TimeUnit.SECONDS, true);
            return;
        }
		
		musicManager.scheduler.queue.clear();
		
        builder.setTitle("Queue Cleared");
        builder.setDescription(e.getMember().getEffectiveName() + " cleared the queue!");
        sendEmbed(e, builder, 1L, TimeUnit.MINUTES, false);
	}

}
