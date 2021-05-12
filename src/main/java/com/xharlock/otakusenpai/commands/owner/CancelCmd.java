package com.xharlock.otakusenpai.commands.owner;

import java.time.Instant;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.core.Bootstrap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CancelCmd extends Command {
	
    public CancelCmd(String name) {
        super(name);
        setDescription("(Owner-only) Use this command to cancel all ongoing requests");
        setUsage(name);
        setIsOwnerCommand(true);
        setCommandCategory(CommandCategory.OWNER);
    }
    
    @Override
    public void onCommand(MessageReceivedEvent e) {
    	e.getMessage().delete().queue();
        e.getJDA().cancelRequests();
        e.getJDA().openPrivateChannelById(Bootstrap.otakuSenpai.getConfig().getOwnerId()).queue(channel -> {
        	EmbedBuilder builder = new EmbedBuilder();
        	builder.setTitle("Success");
        	builder.setDescription("Cancelled all requests");
        	builder.setTimestamp(Instant.now());
        	channel.sendMessage(builder.build()).queue();
        });
    }
}