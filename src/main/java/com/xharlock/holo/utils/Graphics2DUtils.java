package com.xharlock.holo.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Graphics2DUtils {

	public static void drawCenteredString(Graphics2D g2, String text, Rectangle rect, Font font, Color color) {
	    FontMetrics metrics = g2.getFontMetrics(font);
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    g2.setColor(color);
	    g2.setFont(font);
	    g2.drawString(text, x, y);
	}
	
}
