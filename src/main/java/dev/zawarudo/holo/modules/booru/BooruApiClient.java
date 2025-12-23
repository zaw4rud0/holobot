package dev.zawarudo.holo.modules.booru;

import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;

import java.util.ArrayList;
import java.util.List;

public abstract class BooruApiClient<T> {

    protected int limit;
    protected final List<String> tags;
    protected final List<String> blacklisted;
    protected Order order;
    protected Rating rating;

    protected BooruApiClient() {
        limit = 10;
        tags = new ArrayList<>();
        blacklisted = new ArrayList<>();
        order = Order.DEFAULT;
        rating = Rating.ALL;
    }

    /**
     * Sets tags the fetched posts should have.
     *
     * @param tags The tags as Strings.
     */
    public BooruApiClient<T> setTags(String... tags) {
        clearTags();
        this.tags.addAll(List.of(tags));
        return this;
    }

    /**
     * Clears the list of tags to be used.
     */
    public BooruApiClient<T> clearTags() {
        tags.clear();
        return this;
    }

    /**
     * Returns a list of the current tags.
     */
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Sets tags to be blacklisted. Posts with any of these tags will be ignored.
     */
    public BooruApiClient<T> setBlacklistedTags(String... tags) {
        blacklisted.addAll(List.of(tags));
        return this;
    }

    /**
     * Clears the list of blacklisted tags.
     */
    public BooruApiClient<T> clearBlacklistedTags() {
        blacklisted.clear();
        return this;
    }

    /**
     * Returns a list of the current blacklisted tags.
     */
    public List<String> getBlacklistedTags() {
        return new ArrayList<>(blacklisted);
    }

    /**
     * Sets the limit of posts to be returned. By default, the value is 10.
     */
    public BooruApiClient<T> setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Returns the current limit of posts to be returned.
     */
    public int getLimit() {
        return limit;
    }

    public BooruApiClient<T> setOrder(Order order) {
        this.order = order;
        return this;
    }

    public Order getOrder() {
        return order;
    }

    public BooruApiClient<T> setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public Rating getRating() {
        return rating;
    }

    public abstract List<T> getPosts() throws InvalidRequestException, APIException;

    public abstract List<T> getAllPosts() throws InvalidRequestException, APIException;

    public abstract List<T> getAllPosts(int page) throws APIException, InvalidRequestException;

    /**
     * Defines the ordering of the posts.
     */
    public enum Order {
        /**
         * Random order. Replace N with a positive
         * integer to get a random sample of N posts.
         */
        RANDOM("random:N"),
        DEFAULT(""),
        /**
         * Order by ID in ascending order.
         */
        ID_ASC("order:id_asc"),
        /**
         * Order by ID in descending order.
         */
        ID_DESC("order:id_desc"),
        /**
         * Order by score in ascending order.
         */
        SCORE_ASC("order:score_asc"),
        /**
         * Order by score in descending order.
         */
        SCORE_DESC("order:score_desc"),
        /**
         * Order by rank in descending order.
         */
        RANK("order:rank"),
        /**
         * Order by downvotes in descending order.
         */
        DOWNVOTES("order:downvotes"),
        /**
         * Order by upvotes in descending order.
         */
        UPVOTES("order:upvotes");

        private final String value;

        Order(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * The content rating of the posts.
     */
    public enum Rating {
        ALL("", ""),
        SAFE("rating:s", "s"),
        QUESTIONABLE("rating:q", "q"),
        EXPLICIT("rating:e", "e");

        private final String value;
        private final String shortValue;

        Rating(String value, String shortValue) {
            this.value = value;
            this.shortValue = shortValue;
        }

        public String getValue() {
            return value;
        }

        public String getShortValue() {
            return shortValue;
        }
    }
}