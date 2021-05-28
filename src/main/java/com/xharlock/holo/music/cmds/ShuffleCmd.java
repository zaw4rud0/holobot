package com.xharlock.holo.music.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.music.core.*;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShuffleCmd extends MusicCommand {

	public ShuffleCmd(String name) {
		super(name);
		setDescription("Use this command to randomize the order of the tracks in the queue");
		setUsage(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());		
		EmbedBuilder builder = new EmbedBuilder();
		
		if (musicManager.scheduler.queue.isEmpty()) {
            builder.setTitle("Error");
            builder.setDescription("I can't shuffle an empty queue!");
            sendEmbed(e, builder, 15L, TimeUnit.SECONDS, true);
            return;
        }
		
		musicManager.scheduler.shuffle();
        builder.setTitle("Shuffled Queue");
        builder.setDescription(e.getMember().getEffectiveName() + " shuffled the queue!");
        sendEmbed(e, builder, 1L, TimeUnit.MINUTES, false);
	}

}
