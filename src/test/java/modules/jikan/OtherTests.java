package modules.jikan;

import dev.zawarudo.holo.modules.jikan.JikanAPI;
import dev.zawarudo.holo.modules.jikan.model.Anime;
import dev.zawarudo.holo.modules.jikan.model.Related;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OtherTests {

    @BeforeAll
    static void setup() {
        JikanAPI.setLimit(50);
    }

    @Test
    void testRelated() throws APIException, InvalidRequestException, InvalidIdException {
        Anime onePiece = JikanAPI.getAnime(21);

        assertNotNull(onePiece);

        List<Related> related = onePiece.getRelated();

        assertNotNull(related);
        assertFalse(related.isEmpty());
    }
}