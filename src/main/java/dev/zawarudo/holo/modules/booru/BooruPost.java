package dev.zawarudo.holo.modules.booru;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public abstract class BooruPost implements Comparable<BooruPost> {

    @SerializedName("id")
    protected int id;
    @SerializedName("score")
    protected int score;
    @SerializedName("rating")
    protected String rating;
    @SerializedName("file_url")
    protected String url;

    public BooruPost(int id, int score, String rating, String url) {
        this.id = id;
        this.score = score;
        this.rating = rating;
        this.url = url;
    }

    /**
     * Returns the Booru ID of this post.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the score of this post.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the rating of this post. There are three ratings:
     * <ul><li><b>s:</b> Safe</li>
     * <li><b>q:</b> Questionable</li>
     * <li><b>e:</b> Explicit</li></ul>
     * Note that the <b>Safe</b> rating does not mean "Safe for work".
     * For more information, see the
     * <a href="https://danbooru.donmai.us/wiki_pages/howto%3Arate">Danbooru wiki</a>.
     */
    public String getRating() {
        return rating;
    }

    /**
     * Returns the url of the image.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the tags of the post.
     *
     * @return A String array containing the tags.
     */
    public abstract String[] getTags();

    @Override
    public int compareTo(@NotNull BooruPost o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return String.format("Post[id=%d, score=%d, rating=%s, url=%s]", id, score, rating, url);
    }
}