package com.xharlock.holo.misc;

import com.xharlock.holo.core.Bootstrap;

import java.awt.*;

/**
 * Enum to store the colors used in the embeds of Holo.
 */
public enum EmbedColor {

    /** The default color of Holo */
    DEFAULT(Bootstrap.holo.getConfig().getDefaultColor()),
    /** The Discord default embed color */
    NONE(null),
    /** The embed color for the error embed */
    ERROR(Color.RED),
    /** The color of Akinator */
    AKINATOR(new Color(112, 28, 84)),
    /** The color of Gelbooru */
    GELBOORU(new Color(0, 100, 225)),
    /** The color of Inspirobot.me */
    INSPIRO(new Color(35, 96, 19)),
    /** The color of MyAnimeList */
    MAL(new Color(46, 81, 162)),
    WHITE(Color.WHITE),
    LIGHT_GRAY(Color.LIGHT_GRAY);

    private final Color color;

    EmbedColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the color of the embed.
     */
    public Color getColor() {
        return color;
    }
}