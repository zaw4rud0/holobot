package modules.jikan;

import java.util.List;
import java.util.Locale;

import dev.zawarudo.holo.modules.jikan.JikanAPI;
import dev.zawarudo.holo.modules.jikan.model.Anime;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AnimeTests {

    @Test
    void testAnime1() throws APIException, InvalidIdException {
        Anime onePiece = JikanAPI.getAnime(21);
        assertNotNull(onePiece);
        assertEquals(21, onePiece.getId());
        assertEquals("one piece", onePiece.getTitle().toLowerCase(Locale.UK));
        assertEquals("Sundays", onePiece.getBroadcast().getDay());
    }

    @Test
    void testAnime2() throws APIException, InvalidIdException {
        Anime blackClover = JikanAPI.getAnime(34572);
        assertNotNull(blackClover);
        assertEquals(34572, blackClover.getId());
        assertEquals("black clover", blackClover.getTitle().toLowerCase(Locale.UK));
    }

    @Test
    void testAnime3() throws APIException, InvalidIdException {
        Anime overlord = JikanAPI.getAnime(29803);
        assertNotNull(overlord);
        assertEquals(29803, overlord.getId());
    }

    @Test
    void testInvalidId() {
        assertThrows(InvalidIdException.class, () -> JikanAPI.getAnime(0));
        assertThrows(InvalidIdException.class, () -> JikanAPI.getAnime(-1));
    }

    @Test
    void testAnimeSearch1() throws APIException, InvalidRequestException {
        List<Anime> results = JikanAPI.searchAnime("one piece");
        assertNotNull(results);

        boolean correct = false;
        for (Anime res : results) {
            if (res.getTitle().equals("One Piece")) {
                correct = true;
                break;
            }
        }
        assertTrue(correct);
    }

    @Test
    void testRandomAnime() throws APIException {
        Anime randomAnime = JikanAPI.getRandomAnime();
        assertNotNull(randomAnime);
        assertTrue(randomAnime.getId() != 0);
        assertNotNull(randomAnime.getTitle());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 30, 100})
    void testTopAnimes(int number) throws APIException {
        List<Anime> topAnimes = JikanAPI.getTopAnimes(number);
        assertNotNull(topAnimes);
        assertEquals(number, topAnimes.size());
    }
}