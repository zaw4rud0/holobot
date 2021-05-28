package com.xharlock.holo.place;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

public class Compare {

	public static void main(String[] args) throws IOException {

		String mode = "bot";
		boolean random = false;

		BufferedImage img = ImageIO.read(new File("C:/Users/adria/Desktop/todoall.png"));
		BufferedImage place = ImageIO.read(new File("C:/Users/adria/Desktop/place.png"));

		File text_file = new File("C:/Users/adria/Desktop/bot.txt");

		if (img.getWidth() != 1000 || img.getHeight() != 1000) {
			System.out.println("Image needs to be 1000x1000!");
			return;
		}

		List<String> lines = new ArrayList<>();

		if (mode.equals("bot")) {

			
				for (int j = 0; j < 1000; j++) {
					for (int i = 0; i < 1000; i++) {

					Color colorI = new Color(img.getRGB(i, j), true);
					Color colorP = new Color(place.getRGB(i, j), true);
					if (colorI.getAlpha() > 230) {
						if (colorI.getRed() != colorP.getRed() || colorI.getGreen() != colorP.getGreen()
								|| colorI.getBlue() != colorP.getBlue()) {
							lines.add(".place setpixel " + i + " " + j + " " + rgbToHex(colorI) + "\n");
						}
					}
				}
			}

		}
		/*
		 * else if (mode.equals("hand")) { int counter = 0;
		 * 
		 * for (int j = 999; j >= 0; j--) { for (int i = 999; i >= 0; i--) {
		 * 
		 * if (counter >= 3600) { break; }
		 * 
		 * Color colorI = new Color(img.getRGB(i, j), true); Color colorP = new
		 * Color(place.getRGB(i, j), true); if (colorI.getAlpha() > 230) { if
		 * (colorI.getRed() != colorP.getRed() || colorI.getGreen() != colorP.getGreen()
		 * || colorI.getBlue() != colorP.getBlue()) { counter++; writer.print(i + " " +
		 * j + " " + rgbToHex(colorI) + "|"); } } } }
		 * 
		 * }
		 */

		else if (mode.equals("hand")) {
			int counter = 0;
			for (int j = 0; j < 1000; j++) {
				for (int i = 0; i < 1000; i++) {

					if (counter >= 86400) {
						break;
					}

					Color colorI = new Color(img.getRGB(i, j), true);
					Color colorP = new Color(place.getRGB(i, j), true);
					if (colorI.getAlpha() > 230) {
						if (colorI.getRed() != colorP.getRed() || colorI.getGreen() != colorP.getGreen()
								|| colorI.getBlue() != colorP.getBlue()) {
							counter++;
							lines.add(i + " " + j + " " + rgbToHex(colorI) + "|");
						}
					}
				}
			}
		}

		// Random true false
		if (random)
			Collections.shuffle(lines);

		// write to file
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(text_file));
			Iterator<String> it = lines.iterator();
			while (it.hasNext()) {
				pw.write(it.next());
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String rgbToHex(Color c) {
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}

}
