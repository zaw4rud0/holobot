package dev.zawarudo.holo.modules.jikan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents an anime.
 */
public final class Anime extends AbstractMedium<Anime> {
    @SerializedName("source")
    private String source;
    @SerializedName("episodes")
    private int episodes;
    @SerializedName("airing")
    private boolean airing;
    @SerializedName("duration")
    private String duration;
    @SerializedName("rating")
    private String rating;
    @SerializedName("season")
    private String season;
    @SerializedName("year")
    private int year;
    @SerializedName("broadcast")
    private Broadcast broadcast;
    @SerializedName("producers")
    private List<Nameable> producers;
    @SerializedName("licensors")
    private List<Nameable> licensors;
    @SerializedName("studios")
    private List<Nameable> studios;

    public String getSource() {
        return source;
    }

    public int getEpisodes() {
        return episodes;
    }

    /**
     * Whether this anime is currently airing.
     */
    public boolean isAiring() {
        return airing;
    }

    public String getRating() {
        return rating;
    }

    public String getDuration() {
        return duration;
    }

    public String getSeason() {
        if (season != null) {
            return season + " " + year;
        }
        return null;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    public void changeBroadcastTimeZone(String timeZone) {
        broadcast = broadcast.convertToTimeZone(timeZone);
    }

    public List<Nameable> getProducers() {
        return producers;
    }

    public List<Nameable> getLicensors() {
        return licensors;
    }

    public List<Nameable> getStudios() {
        return studios;
    }
}