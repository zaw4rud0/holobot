package dev.zawarudo.holo.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public final class FontUtils {

    private FontUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Enables high-quality rendering for shapes and text.
     */
    public static void enableHighQuality(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }

    /**
     * Returns a rotated version of the given font.
     */
    public static Font rotate(Font font, double degrees) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(degrees), 0, 0);
        return font.deriveFont(at);
    }

    /**
     * Loads a font from src/main/resources/fonts/{fontName}.ttf.
     */
    public static Font loadFontFromFile(String fontName, float fontSize) {
        String resourcePath = "/fonts/" + fontName + ".ttf";

        try (InputStream is = FontUtils.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Font resource not found: " + resourcePath);
            }
            Font base = Font.createFont(Font.TRUETYPE_FONT, is);
            return base.deriveFont(fontSize);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException("Error loading font from resource: " + resourcePath, e);
        }
    }
}