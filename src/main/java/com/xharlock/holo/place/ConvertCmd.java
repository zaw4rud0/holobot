package com.xharlock.holo.place;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.xharlock.holo.utils.BufferedImageOperations;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

public class ConvertCmd extends PlaceCommand {

	public ConvertCmd(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		Message msg = e.getMessage();

		if (args.length != 5 && args.length != 6) {
			e.getChannel().sendMessage("Wrong usage!").queue();
			return;
		}

		if (args.length == 5 && e.getMessage().getAttachments().size() != 1) {
			e.getChannel().sendMessage("Please provide one attachment in your message").queue();
			return;
		}

		List<String> lines = new ArrayList<String>();

		try {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			int width = Integer.parseInt(args[2]);
			int height = Integer.parseInt(args[3]);
			
			if (x < 0 || y < 0 || width < 0 || height < 0) {
				e.getChannel().sendMessage("Values can't be less than 0!").queue();
				return;
			}
			
			boolean random = Boolean.parseBoolean(args[4]);
			
			BufferedImage img = null;
			
			if (e.getMessage().getAttachments().size() != 1) {
				System.out.println(String.join(" ", args));
				img = ImageIO.read(new URL(this.getArgs()[5]));
			} else {
				System.out.println(String.join(" ", args) + " "
						+ msg.getAttachments().get(0).getUrl());
				img = ImageIO.read(new URL(msg.getAttachments().get(0).getUrl()));
			}
			
			e.getMessage().delete().queue();
			img = BufferedImageOperations.resize(img, width, height);
			
			int[][] rgbs = BufferedImageOperations.getRGB(img);
			for (int i = 0; i < img.getHeight(); ++i) {
				for (int j = 0; j < img.getWidth(); ++j) {
					final Color color = new Color(rgbs[j][i], true);
					if (color.getAlpha() != 0 && j + x < 1000 && i + y < 1000) {
						final String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(),
								color.getBlue());
						lines.add(".place setpixel " + (j + x) + " " + (i + y) + " " + hex);
					}
				}
			}
			
			if (random)
				Collections.shuffle(lines);
			
			File output = new File("./output/commands-" + lines.size() + "lines.txt");
			PrintWriter pw = new PrintWriter(new FileWriter(output));
			
			for (int k = 0; k < lines.size(); k++)
				pw.write(String.valueOf(lines.get(k)) + "\n");
				
			pw.close();
			
			e.getChannel().sendFile(output, output.getName(), new AttachmentOption[0]).queue();
			output.delete();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
