package dev.zawarudo.holo.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO: Refactor this class
 */
public final class ImageOperations {

    private ImageOperations() {
    }

    /**
     * Crops an image to a circle
     */
    public static BufferedImage cropToCircle(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(new Ellipse2D.Double(0, 0, w, h));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
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
            Graphics2D g2 = result.createGraphics();
            Color oldColor = g2.getColor();
            g2.setPaint(Color.WHITE);
            g2.fillRect(0, 0, width, height);
            g2.setColor(oldColor);
            g2.drawImage(img1, null, 0, 0);
            g2.drawImage(img2, null, img1.getWidth(), 0);
            g2.dispose();
        } else if (direction == Direction.VERTICAL) {
            int height = img1.getHeight() + img2.getHeight();
            int width = Math.min(img1.getWidth(), img2.getWidth());
            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = result.createGraphics();
            Color oldColor = g2.getColor();
            g2.setPaint(Color.WHITE);
            g2.fillRect(0, 0, width, height);
            g2.setColor(oldColor);
            g2.drawImage(img1, null, 0, 0);
            g2.drawImage(img2, null, 0, img1.getHeight());
            g2.dispose();
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
            Graphics2D g2 = result.createGraphics();
            Color oldColor = g2.getColor();

            g2.setPaint(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            g2.setColor(oldColor);
            g2.drawImage(img1, null, 0, 0);
            g2.drawImage(img2, null, img1.getWidth(), 0);
            g2.drawImage(img3, null, img1.getWidth() + img2.getWidth(), 0);
            g2.dispose();
        } else if (direction == Direction.VERTICAL) {
            int height = img1.getHeight() + img2.getHeight() + img3.getHeight();
            int width = Math.min(Math.min(img1.getWidth(), img2.getWidth()), img3.getWidth());

            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = result.createGraphics();
            Color oldColor = g2.getColor();

            g2.setPaint(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            g2.setColor(oldColor);
            g2.drawImage(img1, null, 0, 0);
            g2.drawImage(img2, null, 0, img1.getHeight());
            g2.drawImage(img3, null, 0, img1.getHeight() + img2.getHeight());
            g2.dispose();
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
     * Cuts a {@link BufferedImage} to a square. The square will be taken from the middle of
     * the image
     *
     * @param image  = {@link BufferedImage} to perform this method on
     * @param length = Side length of the square
     * @return A new {@link BufferedImage}
     */
    public static BufferedImage squarefyCut(BufferedImage image, int length, Color color) {
        BufferedImage temp;
        BufferedImage res = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(color);
        g2.fillRect(0, 0, length, length);
        g2.setColor(oldColor);
        if (image.getWidth() < image.getHeight()) {
            int height = length * image.getHeight() / image.getWidth();
            temp = resize(image, length, height);
            g2.drawImage(temp, null, 0, length / 2 - height / 2);
        } else if (image.getWidth() > image.getHeight()) {
            int width = length * image.getWidth() / image.getHeight();
            temp = resize(image, width, length);
            g2.drawImage(temp, null, length / 2 - width / 2, 0);
        } else {
            temp = resize(image, length, length);
            g2.drawImage(temp, null, 0, 0);
        }
        g2.dispose();
        return res;
    }

    /**
     * Resizes a {@link BufferedImage} to a square. If it's a rectangle, the background will
     * be visible on the sides
     *
     * @param image  = {@link BufferedImage} to perform this method on
     * @param length = Side length of the square
     * @param color  = Background color
     * @return A new {@link BufferedImage}
     */
    public static BufferedImage squarefyResize(BufferedImage image, int length, Color color) {
        BufferedImage temp;
        BufferedImage res = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(color);
        g2.fillRect(0, 0, length, length);
        g2.setColor(oldColor);
        if (image.getWidth() > image.getHeight()) {
            int height = length * image.getHeight() / image.getWidth();
            temp = resize(image, length, height);
            g2.drawImage(temp, null, 0, length / 2 - height / 2);
        } else if (image.getWidth() < image.getHeight()) {
            int width = length * image.getWidth() / image.getHeight();
            temp = resize(image, width, length);
            g2.drawImage(temp, null, length / 2 - width / 2, 0);
        } else {
            temp = resize(image, length, length);
            g2.drawImage(temp, null, 0, 0);
        }
        g2.dispose();
        return res;
    }

    public static BufferedImage squarefy(BufferedImage image, int length, Color color, boolean cut) {
        BufferedImage result = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(0, 0, length, length);
        int x = 0, y = 0, width = image.getWidth(), height = image.getHeight();

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
     * Turns a BufferedImage completely black while ignoring fully transparent pixels.
     */
    public static BufferedImage turnBlack(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color pixel = new Color(img.getRGB(i, j), true);
                if (pixel.getAlpha() == 0) {
                    continue;
                }
                int alpha = pixel.getAlpha();
                img.setRGB(i, j, alpha << 24 + Color.BLACK.getRGB());
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
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public static int[][] getRGBGrid(BufferedImage img) {
        int[][] rgb = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                rgb[j][i] = img.getRGB(j, i);
            }
        }
        return rgb;
    }

    public static BufferedImage convertToGrayScale(BufferedImage img) {
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                Color color = new Color(img.getRGB(i, j));
                int rgb = (int) (color.getRed() * 0.299);
                rgb += (int) (color.getGreen() * 0.587 + color.getBlue() * 0.114);
                img.setRGB(i, j, new Color(rgb, rgb, rgb).getRGB());
            }
        }
        return img;
    }

    public static BufferedImage blur(BufferedImage img) {
        BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        int i = 0;
        int max = 400, rad = 10;
        int a1 = 0, r1 = 0, g1 = 0, b1 = 0;
        Color[] color = new Color[max];
        int x, y, x1, y1, d;
        for (x = rad; x < img.getHeight() - rad; x++) {
            for (y = rad; y < img.getWidth() - rad; y++) {
                for (x1 = x - rad; x1 < x + rad; x1++) {
                    for (y1 = y - rad; y1 < y + rad; y1++) {
                        color[i++] = new Color(img.getRGB(y1, x1));
                    }
                }
                i = 0;
                for (d = 0; d < max; d++) {
                    a1 = a1 + color[d].getAlpha();
                }
                a1 = a1 / (max);
                for (d = 0; d < max; d++) {
                    r1 = r1 + color[d].getRed();
                }
                r1 = r1 / (max);
                for (d = 0; d < max; d++) {
                    g1 = g1 + color[d].getGreen();
                }
                g1 = g1 / (max);
                for (d = 0; d < max; d++) {
                    b1 = b1 + color[d].getBlue();
                }
                b1 = b1 / (max);
                int sum1 = (a1 << 24) + (r1 << 16) + (g1 << 8) + b1;
                output.setRGB(y, x, sum1);
            }
        }
        return output;
    }

    /**
     * The direction in which {@link BufferedImage}s should be joined.
     */
    public enum Direction {
        VERTICAL,
        HORIZONTAL
    }
}