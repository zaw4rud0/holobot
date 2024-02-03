package dev.zawarudo.holo.modules.jikan.model;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("mal_id")
    private int malId;
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;
    @SerializedName("count")
    private int count;

    public int getId() {
        return malId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }
}