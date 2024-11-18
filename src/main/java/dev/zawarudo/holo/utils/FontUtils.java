package dev.zawarudo.holo.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public final class FontUtils {

    private FontUtils() {
        throw new UnsupportedOperationException();
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

    public static Font loadFontFromFile(String fontName, float fontSize) {
        String path = String.format("fonts/%s.ttf", fontName);
        try (InputStream is = FontUtils.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException(String.format("Resource not found in %s.", path));
            }
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(fontSize);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException("Error loading font file!", e);
        }
    }
}