package com.xharlock.holo.music.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.music.core.MusicCommand;
import com.xharlock.holo.music.core.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayCmd extends MusicCommand {

	public PlayCmd(String name) {
		super(name);
		setDescription("Use this command to play a track. If there is already a track playing, it will be added to the queue");
		setUsage(name + " <link>");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();		
		e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		
		if (!isUserInSameChannel(e)) {
			builder.setTitle("Not in same voice channel!");
			builder.setDescription("You need to be in the same voice channel as me!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
        if (args.length == 0) {
        	builder.setTitle("Wrong Usage");
        	builder.setDescription("Please provide a youtube link!");
        	sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
            return;
        }
        
        String link = args[0].replace("<", "").replace(">", "");
        
        if (!isValidURL(link)) {
            builder.setTitle("Invalid Link");
        	builder.setDescription("Please provide a valid youtube link!");
        	sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
            return;
        }
        
        PlayerManager.getInstance().loadAndPlay(e, builder, link);
	}
}
