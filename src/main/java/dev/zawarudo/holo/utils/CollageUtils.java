package dev.zawarudo.holo.utils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating image collages.
 */
public final class CollageUtils {

    private CollageUtils() {
    }

    /**
     * Creates a collage of the specified rows and cols with the given images.
     *
     * @param rows   The number of rows in the collage.
     * @param cols   The number of columns in the collage.
     * @param images The images to be used in the collage.
     * @return The collage image.
     */
    public static BufferedImage createCollage(int rows, int cols, List<BufferedImage> images) {
        if (images.size() != rows * cols) {
            throw new IllegalArgumentException(String.format("This method requires %d images!", rows * cols));
        }

        List<BufferedImage> rowsList = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < rows; i++) {
            List<BufferedImage> colsList = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                colsList.add(images.get(index++));
            }
            rowsList.add(ImageOperations.join(ImageOperations.Direction.HORIZONTAL, colsList.toArray(BufferedImage[]::new)));
        }
        return ImageOperations.join(ImageOperations.Direction.VERTICAL, rowsList.toArray(BufferedImage[]::new));
    }
}