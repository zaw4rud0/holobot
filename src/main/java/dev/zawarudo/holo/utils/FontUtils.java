package dev.zawarudo.holo.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public final class FontUtils {

    private FontUtils() {
    }

    public static void setSmoothFont(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static Font rotateFont(Font font, double ang) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(ang), 0, 0);
        return font.deriveFont(affineTransform);
    }

    public static Font loadFontFromFile(float fontSize) {
        try (InputStream is = FontUtils.class.getResourceAsStream("/ComicSans.ttf")) {
            if (is == null) {
                throw new IllegalStateException("Font file not found at ./src/main/resources/ComicSansBold.ttf");
            }
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(fontSize);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException("Error loading font file!", e);
        }
    }
}