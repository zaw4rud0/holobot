package dev.zawarudo.holo.modules.anime.jikan;

import dev.zawarudo.holo.modules.anime.jikan.JikanApiClient;
import dev.zawarudo.holo.modules.anime.jikan.model.Manga;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MangaTest {

    @Test
    void testMangaById1() throws APIException, InvalidIdException {
        Manga onePiece = JikanApiClient.getManga(13);

        assertNotNull(onePiece);
        assertNotNull(onePiece.getTitle());
        assertNotNull(onePiece.getTitleEnglish());
        assertNotNull(onePiece.getTitleJapanese());
        assertNotNull(onePiece.getTitleSynonyms());
        assertNotNull(onePiece.getType());
        assertNotNull(onePiece.getUrl());
        assertNotNull(onePiece.getImageUrl());
        assertNotNull(onePiece.getImages());
        assertEquals(13, onePiece.getId());
        assertEquals("One Piece", onePiece.getTitle());
    }

    @Test
    void testMangaById2() throws APIException, InvalidIdException {
        Manga naruto = JikanApiClient.getManga(11);

        assertNotNull(naruto);
        assertEquals(11, naruto.getId());
        assertEquals("Naruto", naruto.getTitle());
    }

    @Test
    void testMangaSearch() throws APIException, InvalidRequestException {
        List<Manga> results = JikanApiClient.searchManga("one piece");

        assertNotNull(results);
        assertFalse(results.isEmpty());

        boolean correct = false;
        for (Manga res : results) {
            if (res.getTitle().equals("One Piece")) {
                correct = true;
                break;
            }
        }
        assertTrue(correct);
    }

    @Test
    void testRandomManga() throws APIException {
        Manga randomManga = JikanApiClient.getRandomManga();

        assertNotNull(randomManga);
        assertTrue(randomManga.getId() != 0);
        assertNotNull(randomManga.getTitle());
    }
}