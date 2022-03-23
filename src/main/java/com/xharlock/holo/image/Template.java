package com.xharlock.holo.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.xharlock.holo.utils.BufferedImageOps;

public enum Template {
	CHALLENGER("challenger", new AvatarData(true, 564, 134, 240, 240)),
	LOLICE("lolice", new AvatarData(true, 456, 9, 175, 175));
	
	private String name;
	private AvatarData[] datas;
	private BufferedImage template;
	
	/**
	 * Constructor for a single template
	 * 
	 * @param name = Name of the template
	 */
	Template(String name, AvatarData... avatarDatas) {
		this.name = name;
		
		datas = avatarDatas;
		
		try {
			template = ImageIO.read(new File("./src/main/resources/image/" + name + ".png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Generates an image using the template and the given url
	 */
	public BufferedImage generate(String... urls) throws IOException {
		BufferedImage result = new BufferedImage(template.getWidth(), template.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2 = result.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(template, null, 0, 0);
		
		for (int i = 0; i < datas.length; i++) {
			BufferedImage pfp = ImageIO.read(new URL(urls[i]));
			if (datas[0].circle) {
				pfp = BufferedImageOps.cropToCircle(pfp);
			}
			g2.drawImage(pfp, datas[0].x, datas[0].y, datas[0].width, datas[0].height, null);
		}
		g2.dispose();
		return result;
	}
	
	/**
	 * Returns the name of the template
	 */
	public String getName() {
		return name;
	}	
}

/**
 * Stores the values required for an image on the template
 */
class AvatarData {
	boolean circle;
	int x, y;
	int width, height;
	double rotation;
	
	/**
	 * Constructor for an AvatarData object
	 * 
	 * @param circle = Whether or not the avatar should be cropped to a circle
	 * @param x = Position of the avatar in the x-axis
	 * @param y = Position of the avatar in the y-axis
	 * @param width = Width of the avatar
	 * @param height = Height of the avatar
	 */
	public AvatarData(boolean circle, int x, int y, int width, int height) {
		this.circle = circle;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}