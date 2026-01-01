package dev.zawarudo.holo.modules.anime.jikan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the appearance of a character in an anime or manga.
 */
public class Appearance {

    @SerializedName("role")
    private String role;
    @SerializedName(value = "anime", alternate = {"manga"})
    private Media media;

    /**
     * The anime or manga where the referenced character appears.
     */
    public static class Media {
        @SerializedName("mal_id")
        private int id;
        @SerializedName("url")
        private String url;
        @SerializedName("images")
        private Images images;
        @SerializedName("title")
        private String title;

        /**
         * Returns the MyAnimeList ID of the anime or manga.
         */
        public int getId() {
            return id;
        }

        /**
         * Returns the MyAnimeList URL of the anime or manga.
         */
        public String getUrl() {
            return url;
        }

        /**
         * Returns the images of the referenced character in the anime or manga.
         */
        public Images getImages() {
            return images;
        }

        /**
         * Returns the title of the anime or manga.
         */
        public String getTitle() {
            return title;
        }
    }

    /**
     * Returns the role of the character in the anime or manga.
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the anime or manga the character appears in.
     */
    public Media getMedia() {
        return media;
    }
}