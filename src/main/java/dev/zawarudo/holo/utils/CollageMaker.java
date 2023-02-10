package dev.zawarudo.holo.utils;

import java.awt.image.BufferedImage;
import java.util.List;

public final class CollageMaker {

	private CollageMaker() {
	}
	
	/**
	 * Method to stick four BufferedImages together in the form of <br>
	 * # # <br>
	 * # #
	 */
	public static BufferedImage create2x2Collage(List<BufferedImage> images) {
		if (images.size() != 4) {
			throw new IllegalArgumentException("This method requires four images!");
		}
		BufferedImage row1 = ImageOperations.join(images.get(0), images.get(1), ImageOperations.Direction.HORIZONTAL);
		BufferedImage row2 = ImageOperations.join(images.get(2), images.get(3), ImageOperations.Direction.HORIZONTAL);
		return ImageOperations.join(row1, row2, ImageOperations.Direction.VERTICAL);
	}

	/**
	 * Method to stick six images together in the form of <br>
	 * # # # <br>
	 * # # #
	 */
	public static BufferedImage create3x2Collage(List<BufferedImage> images) {
		if (images.size() != 6) {
			throw new IllegalArgumentException("This method requires six images!");
		}
		BufferedImage row1 = ImageOperations.join(images.get(0), images.get(1), images.get(2), ImageOperations.Direction.HORIZONTAL);
		BufferedImage row2 = ImageOperations.join(images.get(3), images.get(4), images.get(5),	ImageOperations.Direction.HORIZONTAL);
		return ImageOperations.join(row1, row2, ImageOperations.Direction.VERTICAL);
	}

	/**
	 * Method to stick nine images together in the form of <br>
	 * # # # <br>
	 * # # # <br>
	 * # # #
	 */
	public static BufferedImage create3x3Collage(List<BufferedImage> images) {
		if (images.size() != 9) {
			throw new IllegalArgumentException("This method requires nine images!");
		}
		BufferedImage row1 = ImageOperations.join(images.get(0), images.get(1), images.get(2),	ImageOperations.Direction.HORIZONTAL);
		BufferedImage row2 = ImageOperations.join(images.get(3), images.get(4), images.get(5),	ImageOperations.Direction.HORIZONTAL);
		BufferedImage row3 = ImageOperations.join(images.get(6), images.get(7), images.get(8),	ImageOperations.Direction.HORIZONTAL);
		return ImageOperations.join(row1, row2, row3, ImageOperations.Direction.VERTICAL);
	}
}