package com.xharlock.holo.place;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CompareCmd extends PlaceCommand {

	public CompareCmd(String name) {
		super(name);
		
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		EmbedBuilder builder = new EmbedBuilder();
		BufferedImage img = null;
		
		// Image link, needs to be 1000x1000 pixels
		if (args.length == 1) {
			try {
				img = ImageIO.read(new URL(args[0]));
			} catch (IOException ex) {
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while reading your image link");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}
			
			if (img.getHeight() != 1000 || img.getWidth() != 1000) {
				builder.setTitle("Unsuitable Image");
				builder.setDescription("Please provide a 1000x1000 image!");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}
		}
		
		// Instructions as attachment
		else if (!e.getMessage().getAttachments().isEmpty()) {
			
			// Attachment needs to be a text file with instructions
			if (!e.getMessage().getAttachments().get(0).getFileExtension().equals("txt")) {
				e.getChannel().sendMessage("Attachment has wrong extension!").queue();
				return;
			}
			
			else {
				
			}
		}
		
		// Wrong usage
		else {
			e.getChannel().sendMessage("Wrong usage!").queue();
			return;
		}
		
		
		
	}
	
	

}
