package com.xharlock.holo.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.BufferedImageOps;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command that generates an image with a given user
 */
public class GenerateCmd extends Command {

	public GenerateCmd(String name) {
		super(name);
		setDescription("");
		setUsage("");
		setAliases(List.of("gen"));
		setIsGuildOnlyCommand(true);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		deleteInvoke(e);
		
		// Show all templates
		if (args.length == 0) {
			builder.setTitle("List of Templates");
			builder.setDescription("");
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
			return;
		}
		
		Template temp = null;
		
		for (Template t : Template.values()) {
			if (args[0].toLowerCase(Locale.UK).equals(t.getName())) {
				temp = t;
				break;
			}
		}
		
		if (temp == null) {
			builder.setTitle("Error");
			builder.setDescription("Please choose a template to generate an image from!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
			return;
		}
		
		sendTyping(e);
		
		
		
		InputStream input = null;
			
		
		try {
			BufferedImage result = temp.generate(e.getGuild().getMemberById(args[0]).getEffectiveAvatarUrl() + "?size=256");
			input = BufferedImageOps.toInputStream(result);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		e.getChannel().sendFile(input, temp.getName() + ".png").queue();
	}
}