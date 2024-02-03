package modules.booru;

import dev.zawarudo.holo.modules.booru.danbooru.DanbooruAPI;
import dev.zawarudo.holo.modules.booru.danbooru.DanbooruPost;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DanbooruPostTest {

    @Test
    public void testPost() throws APIException, InvalidRequestException {
        DanbooruPost post = DanbooruAPI.getPost(1);

        // Basic data
        assertEquals(1, post.getId());
        assertNotNull(post.getCreatedAt());
        assertNotEquals(0, post.getScore());

        // Tags
        assertNotNull(post.getTags());
        assertNotNull(post.getGeneralTags());
        assertNotNull(post.getArtistTags());
        assertNotNull(post.getMetaTags());
        assertNotNull(post.getCharacterTags());
        assertNotNull(post.getCopyrightTags());

        // Links
        assertNotNull(post.getSource());
        assertNotNull(post.getFileExtension());
        assertNotNull(post.getPreviewFileUrl());
        assertNotNull(post.getUrl());
        assertNotNull(post.getImageDimension());
        if (post.hasLargeImage()) {
            assertNotNull(post.getLargeFileUrl());
        }
    }
}