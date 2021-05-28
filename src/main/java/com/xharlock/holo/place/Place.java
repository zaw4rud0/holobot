package com.xharlock.holo.place;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.regex.*;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Place {

	public static Mode mode;
	public static User target;

	private static BufferedImage canvas;

	public Place() throws InterruptedException {		
		canvas = PlaceWebSocket.getPlace(true);
		mode = Mode.NONE;
		target = null;
	}

	public void doStuff(MessageReceivedEvent e) {

		String[] args = e.getMessage().getContentRaw().toLowerCase().split(" ");

		if (args.length == 1) {
			return;
		}
		
		if (args[1].equals("setpixel")) {
			
			if (!isValidHex(args[4].replace("\n", ""))) {
				return;
			}
			
			if (mode == Mode.NONE) {
				try {
					canvas.setRGB(Integer.parseInt(args[2]), Integer.parseInt(args[3]), hex2Rgb(args[4]).getRGB());
				} catch (NumberFormatException ex) {}			
			} else if (mode == Mode.PROTECT) {
				
				BufferedImage img = ProtectCmd.getImage();
				
				Color color = new Color(img.getRGB(Integer.parseInt(args[2]), Integer.parseInt(args[3])));
				
				if (color.getAlpha() == 0) {
					return;
				} else {
					String hex = "#" + Integer
							.toHexString(img.getRGB(Integer.parseInt(args[2]), Integer.parseInt(args[3]))).substring(2);
					e.getChannel().sendMessage(".place setpixel " + args[2] + " " + args[3] + " " + hex).queue();
					//e.getJDA().getTextChannelById(819966095070330950L).sendMessage(".place setpixel " + args[2] + " " + args[3] + " " + hex).queue();
				}
				
			} else if (mode == Mode.BULLY_RESTORE) {
				
				// Check if hex is valid by checking length of 7, example #FFFFFF. If it's not
				// valid, return

				// Georg's bot: 778731540359675904L
				// Ducky: 817846061347242026L
				// Georg: 381154302720213002L
				// Bestbot: 776555901724917800L
				
				if (target != null && e.getAuthor().equals(target)) {					
					// Check if pixel is different than before
					if (hex2Rgb(args[4]).getRGB() != canvas.getRGB(Integer.parseInt(args[2]), Integer.parseInt(args[3]))) {
						
						String color = "#" + Integer
								.toHexString(canvas.getRGB(Integer.parseInt(args[2]), Integer.parseInt(args[3]))).substring(2);
						e.getGuild().getTextChannelById(819966095070330950L)
								.sendMessage(".place setpixel " + args[2] + " " + args[3] + " " + color + " | Undoing " + e.getGuild().getMemberById(target.getIdLong()).getEffectiveName() + "'s pixel").queue();
					}					
				} else {					
					try {
						canvas.setRGB(Integer.parseInt(args[2]), Integer.parseInt(args[3]), hex2Rgb(args[4]).getRGB());
					} catch (NumberFormatException ex) {}
				}
			}

			

		}

		else if (args[1].equals("setmultiplepixels")) {
			String s = e.getMessage().getContentRaw().replace(".place setmultiplepixels ", "");
			String[] pixels = s.split("\\|");
			for (int i = 0; i < pixels.length; i++) {
				String[] arg = pixels[i].split(" ");

				try {
					canvas.setRGB(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]), hex2Rgb(arg[2]).getRGB());
				} catch (NumberFormatException ex) {}
			}
		}

		else {

		}
	}

	public void setPixel(int x, int y, int rgb) {
		canvas.setRGB(x, y, rgb);
	}

	public static BufferedImage getCanvas() {
		return canvas;
	}

	public static List<String> readLines(File file) {
		return null;
	}

	public static Color hex2Rgb(String colorStr) {
		
		// Example: #09F which is the same as #0099FF
		if (colorStr.length() == 4) {
			return new Color(Integer.valueOf("" + colorStr.charAt(1) + colorStr.charAt(1), 16), Integer.valueOf("" + colorStr.charAt(2) + colorStr.charAt(2), 16), Integer.valueOf("" + colorStr.charAt(3) + colorStr.charAt(3), 16));
		}
		
		// Example: #FFFFFF
		else {
			return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16),
					Integer.valueOf(colorStr.substring(5, 7), 16));
		}
		
	}
	
	public static boolean isValidHex(String colorCode) {
		String HEX_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";
		Pattern pattern = Pattern.compile(HEX_PATTERN);
		Matcher matcher = pattern.matcher(colorCode);
		return matcher.matches();
	}
}
