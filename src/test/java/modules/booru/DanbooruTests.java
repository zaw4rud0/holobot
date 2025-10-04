package modules.booru;

import dev.zawarudo.holo.modules.booru.BooruPost;
import dev.zawarudo.holo.modules.booru.danbooru.DanbooruAPI;
import dev.zawarudo.holo.modules.booru.danbooru.DanbooruPost;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DanbooruTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(DanbooruTests.class);

    @Test
    void testPostCount() throws APIException, InvalidRequestException {
        assertTrue(DanbooruAPI.getPostCount("holo") > 2500);
        assertTrue(DanbooruAPI.getPostCount() > 5000000);
        assertEquals(0, DanbooruAPI.getPostCount("holo", "yae_miko"));
    }

    /**
     * Test for the basic functionality and the default settings of Danbooru.
     */
    @Test
    void testBasic() {
        DanbooruAPI danbooru = new DanbooruAPI();

        assertEquals(10, danbooru.getLimit());
        assertEquals(DanbooruAPI.Order.DEFAULT, danbooru.getOrder());
        assertEquals(DanbooruAPI.Rating.ALL, danbooru.getRating());
        assertEquals(new ArrayList<String>(), danbooru.getTags());
        assertEquals(new ArrayList<String>(), danbooru.setTags("holo").clearTags().getTags());
        assertEquals(new ArrayList<String>(), danbooru.getBlacklistedTags());
        assertEquals(new ArrayList<String>(), danbooru.setBlacklistedTags("holo").clearBlacklistedTags().getBlacklistedTags());

        List<DanbooruPost> posts;

        try {
            posts = danbooru.getPosts();
        } catch (InvalidRequestException | APIException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(posts);
        assertEquals(10, posts.size());

        for (BooruPost post : posts) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(post.toString());
            }
        }
    }

    @Test
    void testLimit() throws APIException, InvalidRequestException {
        DanbooruAPI danbooru = new DanbooruAPI();

        List<DanbooruPost> posts = danbooru.getPosts();
        assertNotNull(posts);
        assertEquals(10, posts.size());

        posts = danbooru.setLimit(100).getPosts();
        assertNotNull(posts);
        assertEquals(100, posts.size());

        posts = danbooru.setLimit(200).getPosts();
        assertNotNull(posts);
        assertEquals(200, posts.size());

        posts = danbooru.setLimit(300).getPosts();
        assertNotNull(posts);
        assertNotEquals(300, posts.size());
    }

    @Test
    void testRatingSafe() throws APIException, InvalidRequestException {
        DanbooruAPI danbooru = new DanbooruAPI();
        List<DanbooruPost> posts = danbooru.setRating(DanbooruAPI.Rating.SAFE).getPosts();

        for (BooruPost post : posts) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(post.toString());
            }
            assertEquals(DanbooruAPI.Rating.SAFE.getShortValue(), post.getRating());
        }
    }

    @Test
    void testRatingQuestionable() throws APIException, InvalidRequestException {
        DanbooruAPI danbooru = new DanbooruAPI();
        List<DanbooruPost> posts = danbooru.setRating(DanbooruAPI.Rating.QUESTIONABLE).getPosts();

        for (BooruPost post : posts) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(post.toString());
            }
            assertEquals(DanbooruAPI.Rating.QUESTIONABLE.getShortValue(), post.getRating());
        }
    }

    @Test
    void testRatingExplicit() throws APIException, InvalidRequestException {
        DanbooruAPI danbooru = new DanbooruAPI();
        List<DanbooruPost> posts = danbooru.setRating(DanbooruAPI.Rating.EXPLICIT).getPosts();

        for (BooruPost post : posts) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(post.toString());
            }
            assertEquals(DanbooruAPI.Rating.EXPLICIT.getShortValue(), post.getRating());
        }
    }
}