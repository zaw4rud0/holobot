package com.xharlock.otakusenpai.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageDownloader {

	public static void downloadImage(String url, String filePath, String name) throws IOException, Exception {
		BufferedImage res = null;
		if (!url.endsWith(".png") && !url.endsWith(".jpg"))
			throw new IllegalArgumentException("Wrong file format");
		res = ImageIO.read(new URL(url));
		if (!ImageIO.write(res, "jpg", new File(String.valueOf(filePath) + "/" + name + ".jpg"))
				&& !ImageIO.write(res, "png", new File(String.valueOf(filePath) + "/" + name + ".png")))
			throw new Exception();
		res.flush();
	}

	public static BufferedImage downloadBufferedImage(String url) throws Exception {
		BufferedImage res = null;		
		if (!url.endsWith(".png") && !url.endsWith(".jpg"))
			throw new IllegalArgumentException("Wrong file format");
		res = ImageIO.read(new URL(url));
		return res;
	}

}
