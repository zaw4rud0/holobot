package dev.zawarudo.holo.image.meme;

import dev.zawarudo.holo.utils.ImageOperations;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public enum Template {
	APPROACH("approach", new AvatarData(true, 100, 6, 150, 150), new AvatarData(true, 485, 111, 100, 100)),
	CHALLENGER("challenger", new AvatarData(true, 564, 134, 240, 240)),
	LOLICE("lolice", new AvatarData(true, 456, 9, 175, 175));
	
	private final String name;
	private final AvatarData[] datas;
	private BufferedImage template;
	
	/**
	 * Constructor for a single template
	 * 
	 * @param name = Name of the template
	 */
	Template(String name, AvatarData... datas) {
		this.name = name;
		this.datas = datas;
		
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
				pfp = ImageOperations.cropToCircle(pfp);
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
	 * @param circle = Whether the avatar should be cropped to a circle
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