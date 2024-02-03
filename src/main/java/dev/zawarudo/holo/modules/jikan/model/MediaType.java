package dev.zawarudo.holo.modules.jikan.model;

/**
 * Represents the media type.
 */
public enum MediaType {
    ANIME("anime"),
    MANGA("manga");

    private final String type;

    MediaType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}