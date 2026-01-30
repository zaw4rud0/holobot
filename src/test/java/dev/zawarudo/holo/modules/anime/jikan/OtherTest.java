package dev.zawarudo.holo.modules.anime.jikan;

import dev.zawarudo.holo.modules.anime.jikan.JikanApiClient;
import dev.zawarudo.holo.modules.anime.jikan.model.Anime;
import dev.zawarudo.holo.modules.anime.jikan.model.Related;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OtherTest {

    @BeforeAll
    static void setup() {
        JikanApiClient.setLimit(50);
    }

    @Test
    void testRelated() throws APIException, InvalidRequestException, InvalidIdException {
        Anime onePiece = JikanApiClient.getAnime(21);

        assertNotNull(onePiece);

        List<Related> related = onePiece.getRelated();

        assertNotNull(related);
        assertFalse(related.isEmpty());
    }
}