package com.xharlock.otakusenpai.commands.cmds;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.misc.Messages;
import com.xharlock.otakusenpai.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InspiroCmd extends Command {

	private final String baseUrl = "https://inspirobot.me/api?generate=true";
	
	public InspiroCmd(String name) {
		super(name);
		setDescription("Use this command to get a random quote from [Inspirobot](https://inspirobot.me/)");
		setUsage(name);
		setCommandCategory(CommandCategory.MISC);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
        EmbedBuilder builder = new EmbedBuilder();
        
        String url = "";
        
        try {
            url = HttpResponse.readLine(baseUrl);
        }
        catch (IOException ex) {
        	builder.setTitle(Messages.TITLE_ERROR.getText());
        	builder.setDescription("Something went wrong! Please try again in a few minutes.");
        	sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
        	return;
        }
        
        builder.setTitle("InspiroBot Quote");
        builder.setImage(url);
        sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);		
	}

}
