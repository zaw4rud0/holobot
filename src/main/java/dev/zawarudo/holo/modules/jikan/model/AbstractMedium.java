package dev.zawarudo.holo.modules.jikan.model;

import com.google.gson.annotations.SerializedName;
import dev.zawarudo.holo.modules.jikan.JikanAPI;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractMedium<T extends AbstractMedium<T>> implements Comparable<T> {

    @SerializedName("mal_id")
    protected int id;
    @SerializedName("url")
    protected String url;
    @SerializedName("images")
    protected Images images;
    @SerializedName("title")
    protected String title;
    @SerializedName("title_english")
    protected String titleEn;
    @SerializedName("title_japanese")
    protected String titleJp;
    @SerializedName("title_synonyms")
    protected List<String> titleSynonyms;
    @SerializedName("type")
    protected String type;
    @SerializedName("status")
    protected String status;
    @SerializedName("score")
    protected double score;
    @SerializedName("scored_by")
    protected long scoredBy;
    @SerializedName("rank")
    protected int rank;
    @SerializedName("popularity")
    protected int popularity;
    @SerializedName("members")
    protected long members;
    @SerializedName("favorites")
    protected long favorites;
    @SerializedName("synopsis")
    protected String synopsis;
    @SerializedName("background")
    protected String background;
    @SerializedName("genres")
    protected List<Nameable> genres;
    @SerializedName("explicit_genres")
    protected List<Nameable> explicitGenres;
    @SerializedName("themes")
    protected List<Nameable> themes;
    @SerializedName("demographics")
    protected List<Nameable> demographics;
    @SerializedName(value = "released", alternate = {"published", "aired"})
    protected Released released;

    /**
     * MyAnimeList ID of this medium.
     */
    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return images.getJpg().getImage();
    }

    public Images getImages() {
        return images;
    }

    public String getTitle() {
        return title;
    }

    public Optional<String> getTitleEnglish() {
        if (titleEn == null || titleEn.equals("null")) {
            return Optional.empty();
        }
        return Optional.of(titleEn);
    }

    public Optional<String> getTitleJapanese() {
        if (titleJp == null || titleJp.equals("null")) {
            return Optional.empty();
        }
        return Optional.of(titleJp);
    }

    public List<String> getTitleSynonyms() {
        return new ArrayList<>(titleSynonyms);
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public double getScore() {
        return score;
    }

    public long getScoredBy() {
        return scoredBy;
    }

    /**
     * The all-time MyAnimeList rank of this medium.
     */
    public int getRank() {
        return rank;
    }

    public int getPopularity() {
        return popularity;
    }

    /**
     * The number of MyAnimeList users who have added this medium in their list.
     */
    public long getMembers() {
        return members;
    }

    /**
     * The number of  MyAnimeList users who have marked this medium as their favorite.
     */
    public long getFavorites() {
        return favorites;
    }

    public Optional<String> getSynopsis() {
        if (synopsis != null) {
            return Optional.of(synopsis.replace("[Written by MAL Rewrite]", "").strip());
        }
        return Optional.empty();
    }

    public String getBackground() {
        return background;
    }

    public List<Nameable> getGenres() {
        return genres;
    }

    public List<Nameable> getExplicitGenres() {
        return explicitGenres;
    }

    public List<Nameable> getThemes() {
        return themes;
    }

    public List<Nameable> getDemographics() {
        return demographics;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbstractMedium<?> media = (AbstractMedium<?>) obj;
        return id == media.id;
    }

    @Override
    public int compareTo(T o) {
        return Integer.compare(id, o.getId());
    }

    @Override
    public String toString() {
        return title;
    }

    public List<Related> getRelated() throws APIException, InvalidRequestException {
        return JikanAPI.getRelated(id, MediaType.ANIME);
    }

    public Released getReleased() {
        return released;
    }
}