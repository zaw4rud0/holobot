package com.xharlock.holo.utils;

import java.awt.image.BufferedImage;
import java.util.List;

public class CollageMaker {

	public static BufferedImage create2x2Collage(List<BufferedImage> images) throws IllegalArgumentException {
		if (images.size() != 4)
			throw new IllegalArgumentException("This method requires four images!");
		BufferedImage row1 = BufferedImageOps.join(images.get(0), images.get(1), "horizontal");
		BufferedImage row2 = BufferedImageOps.join(images.get(2), images.get(3), "horizontal");
		return BufferedImageOps.join(row1, row2, "vertical");
	}

	public static BufferedImage create3x2Collage(List<BufferedImage> images) throws IllegalArgumentException {
		if (images.size() != 6)
			throw new IllegalArgumentException("This method requires six images!");
		BufferedImage row1 = BufferedImageOps.join(images.get(0), images.get(1), images.get(2),
				"horizontal");
		BufferedImage row2 = BufferedImageOps.join(images.get(3), images.get(4), images.get(5),
				"horizontal");
		return BufferedImageOps.join(row1, row2, "vertical");
	}

	public static BufferedImage create3x3Collage(List<BufferedImage> images) throws IllegalArgumentException {
		if (images.size() != 9)
			throw new IllegalArgumentException("This method requires nine images!");
		BufferedImage row1 = BufferedImageOps.join(images.get(0), images.get(1), images.get(2),
				"horizontal");
		BufferedImage row2 = BufferedImageOps.join(images.get(3), images.get(4), images.get(5),
				"horizontal");
		BufferedImage row3 = BufferedImageOps.join(images.get(6), images.get(7), images.get(8),
				"horizontal");
		return BufferedImageOps.join(row1, row2, row3, "vertical");
	}

}
