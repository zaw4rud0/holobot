package com.xharlock.holo.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public final class BufferedImageOps {

	private BufferedImageOps() {
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
	 * @param img1 = First {@link BufferedImage}
	 * @param img2 = Second {@link BufferedImage}
	 * @param direction = Direction in which the images are sticked together, can either be <b>horizontal</b> or <b>vertical</b>
	 * @return The joined images
	 */
	public static BufferedImage join(BufferedImage img1, BufferedImage img2, Direction direction) {
		BufferedImage result = null;

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
	 * @param img1 = First {@link BufferedImage}
	 * @param img2 = Second {@link BufferedImage}
	 * @param img3 = Third {@link BufferedImage}
	 * @param direction = Direction in which the images are sticked together, can either be <b>horizontal</b> or <b>vertical</b>
	 * @return A new {@link BufferedImage}
	 */
	public static BufferedImage join(BufferedImage img1, BufferedImage img2, BufferedImage img3, Direction direction) {
		BufferedImage result = null;

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
		}

		else if (direction == Direction.VERTICAL) {
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
		}
		else {
			throw new IllegalArgumentException("Direction can only be either horizontal or vertical!");
		}
		return result;
	}

	/**
	 * Method to downsize, upsize, stretch, etc. a {@link BufferedImage}
	 * 
	 * @param img  = {@link BufferedImage}
	 * @param newW = New width of the image
	 * @param newH = New height of the image
	 * @return A new {@link BufferedImage}
	 */
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return dimg;
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
		BufferedImage temp = null;
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
		} 
		else if (image.getWidth() > image.getHeight()) {
			int width = length * image.getWidth() / image.getHeight();
			temp = resize(image, width, length);
			g2.drawImage(temp, null, length / 2 - width / 2, 0);
		} 
		else {
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
		BufferedImage temp = null;
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
    
    public static int[][] getRGB(BufferedImage img) {
        int[][] rgbs = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                rgbs[j][i] = img.getRGB(j, i);
            }
        }
        return rgbs;
    }
    
    public static BufferedImage toGrayScale(BufferedImage img) {
        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                Color color = new Color(img.getRGB(i, j));
                int rgb = (int)(color.getRed() * 0.299);
                rgb += (int)(color.getGreen() * 0.587 + color.getBlue() * 0.114);
                img.setRGB(i, j, new Color(rgb, rgb, rgb).getRGB());
            }
        }
        return img;
    }
    
    /**
     * The direction in which {@link BufferedImage}s should be joined.
     */
    public enum Direction {
    	VERTICAL,
    	HORIZONTAL;
    }
}