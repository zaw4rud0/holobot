package dev.zawarudo.holo.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ImageOperations {

    private static final double RED_TO_GRAY_WEIGHT = 0.2126;
    private static final double GREEN_TO_GRAY_WEIGHT = 0.7152;
    private static final double BLUE_TO_GRAY_WEIGHT = 0.0722;

    private ImageOperations() {
    }

    /**
     * Crops an image to a circle
     */
    public static BufferedImage cropToCircle(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = output.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.fill(new Ellipse2D.Double(0, 0, width, height));
        graphics.setComposite(AlphaComposite.SrcAtop);
        graphics.drawImage(img, 0, 0, null);
        graphics.dispose();
        return output;
    }

    /**
     * Sticks two {@link BufferedImage} together in the given direction
     *
     * @param img1      = First {@link BufferedImage}
     * @param img2      = Second {@link BufferedImage}
     * @param direction = Direction in which the images are glued together, can either be <b>horizontal</b> or <b>vertical</b>
     * @return The joined images
     */
    public static BufferedImage join(BufferedImage img1, BufferedImage img2, Direction direction) {
        BufferedImage result;

        if (direction == null) {
            throw new IllegalArgumentException();
        } else if (direction == Direction.HORIZONTAL) {
            int width = img1.getWidth() + img2.getWidth();
            int height = Math.min(img1.getHeight(), img2.getHeight());
            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = result.createGraphics();
            Color oldColor = graphics.getColor();
            graphics.setPaint(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(oldColor);
            graphics.drawImage(img1, null, 0, 0);
            graphics.drawImage(img2, null, img1.getWidth(), 0);
            graphics.dispose();
        } else if (direction == Direction.VERTICAL) {
            int height = img1.getHeight() + img2.getHeight();
            int width = Math.min(img1.getWidth(), img2.getWidth());
            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = result.createGraphics();
            Color oldColor = graphics.getColor();
            graphics.setPaint(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(oldColor);
            graphics.drawImage(img1, null, 0, 0);
            graphics.drawImage(img2, null, 0, img1.getHeight());
            graphics.dispose();
        } else {
            throw new IllegalArgumentException("Direction can only be either horizontal or vertical!");
        }
        return result;
    }

    /**
     * Sticks three {@link BufferedImage} together in the given direction
     *
     * @param img1      = First {@link BufferedImage}
     * @param img2      = Second {@link BufferedImage}
     * @param img3      = Third {@link BufferedImage}
     * @param direction = Direction in which the images are glued together, can either be <b>horizontal</b> or <b>vertical</b>
     * @return A new {@link BufferedImage}
     */
    public static BufferedImage join(BufferedImage img1, BufferedImage img2, BufferedImage img3, Direction direction) {
        BufferedImage result;

        if (direction == Direction.HORIZONTAL) {
            int width = img1.getWidth() + img2.getWidth() + img3.getWidth();
            int height = Math.min(Math.min(img1.getHeight(), img2.getHeight()), img3.getHeight());

            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = result.createGraphics();
            Color oldColor = graphics.getColor();

            graphics.setPaint(Color.WHITE);
            graphics.fillRect(0, 0, width, height);

            graphics.setColor(oldColor);
            graphics.drawImage(img1, null, 0, 0);
            graphics.drawImage(img2, null, img1.getWidth(), 0);
            graphics.drawImage(img3, null, img1.getWidth() + img2.getWidth(), 0);
            graphics.dispose();
        } else if (direction == Direction.VERTICAL) {
            int height = img1.getHeight() + img2.getHeight() + img3.getHeight();
            int width = Math.min(Math.min(img1.getWidth(), img2.getWidth()), img3.getWidth());

            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = result.createGraphics();
            Color oldColor = graphics.getColor();

            graphics.setPaint(Color.WHITE);
            graphics.fillRect(0, 0, width, height);

            graphics.setColor(oldColor);
            graphics.drawImage(img1, null, 0, 0);
            graphics.drawImage(img2, null, 0, img1.getHeight());
            graphics.drawImage(img3, null, 0, img1.getHeight() + img2.getHeight());
            graphics.dispose();
        } else {
            throw new IllegalArgumentException("Direction can only be either horizontal or vertical!");
        }
        return result;
    }

    /**
     * Method to downsize, upsize, stretch, etc. a {@link BufferedImage}.
     *
     * @param img       = The {@link BufferedImage} to resize.
     * @param newWidth  = New width of the image.
     * @param newHeight = New height of the image.
     * @return The resized {@link BufferedImage}.
     */
    public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = newImage.createGraphics();
        try {
            graphics.drawImage(scaledImage, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return newImage;
    }

    /**
     * Improved version of the old squarefy methods:
     * <a href="https://gist.github.com/xHarlock/343febf77f25ce26422527cd0500adcd">Link</a>.
     */
    public static BufferedImage squarefy(BufferedImage image, int length, Color color, boolean cut) {
        BufferedImage result = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(0, 0, length, length);

        int x = 0;
        int y = 0;
        int width = image.getWidth();
        int height = image.getHeight();

        if (cut) {
            if (width > height) {
                x = (width - height) / 2;
                width = height;
            } else {
                y = (height - width) / 2;
                height = width;
            }
        } else {
            if (width > height) {
                height = length * height / width;
                width = length;
                y = (length - height) / 2;
            } else if (height > width) {
                width = length * width / height;
                height = length;
                x = (length - width) / 2;
            } else {
                width = height = length;
            }
        }

        try {
            graphics.drawImage(image, x, y, width, height, null);
        } finally {
            graphics.dispose();
        }
        return result;
    }

    /**
     * Turns an image completely black while ignoring fully transparent pixels.
     *
     * @param img The {@link BufferedImage} to turn black.
     * @return The image but in black.
     */
    public static BufferedImage turnBlack(BufferedImage img) {
        int blackRGB = Color.BLACK.getRGB() & 0x00FFFFFF;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i, j);
                int alpha = (rgb >> 24) & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                img.setRGB(i, j, (alpha << 24) | blackRGB);
            }
        }
        return img;
    }

    /**
     * Converts a {@link BufferedImage} into an {@link InputStream}
     *
     * @param img = A {@link BufferedImage}
     * @return An {@link InputStream}
     */
    public static InputStream toInputStream(BufferedImage img) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Turns a given image to grayscale.
     *
     * @param image The {@link BufferedImage} to grayscale.
     * @return The grayscaled image.
     */
    public static BufferedImage convertToGrayScale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = image.getRGB(col, row);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                int grayscaleValue = calculateGrayscaleValue(red, green, blue);
                Color grayscaleColor = new Color(grayscaleValue, grayscaleValue, grayscaleValue);
                grayscaleImage.setRGB(col, row, grayscaleColor.getRGB());
            }
        }
        return grayscaleImage;
    }

    private static int calculateGrayscaleValue(int red, int green, int blue) {
        return (int) (RED_TO_GRAY_WEIGHT * red + GREEN_TO_GRAY_WEIGHT * green + BLUE_TO_GRAY_WEIGHT * blue);
    }

    /**
     * The direction in which {@link BufferedImage}s should be joined.
     */
    public enum Direction {
        VERTICAL,
        HORIZONTAL
    }
}