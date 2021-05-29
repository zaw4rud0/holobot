package com.xharlock.holo.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

@Deprecated
public class ImageDownloader {
	
	public static BufferedImage downloadBufferedImage(String url) throws IllegalArgumentException, IOException {
		BufferedImage res = null;		
		if (!url.endsWith(".png") && !url.endsWith(".jpg") && !url.endsWith(".jpeg"))
			throw new IllegalArgumentException("Wrong file format");
		res = ImageIO.read(new URL(url));
		return res;
	}
}
