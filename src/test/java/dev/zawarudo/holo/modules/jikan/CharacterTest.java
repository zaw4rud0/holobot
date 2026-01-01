package dev.zawarudo.holo.modules.jikan;

import dev.zawarudo.holo.modules.anime.jikan.JikanApiClient;
import dev.zawarudo.holo.modules.anime.jikan.model.Appearance;
import dev.zawarudo.holo.modules.anime.jikan.model.Character;
import dev.zawarudo.holo.modules.anime.jikan.model.MediaType;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CharacterTest {

    @Test
    void testCharacterById() throws APIException, InvalidIdException {
        Character luffy = JikanApiClient.getCharacter(40);

        assertNotNull(luffy);

        assertEquals(40, luffy.getId());
        assertEquals("Luffy Monkey D.", luffy.getName());
        assertTrue(luffy.getFavorites() != 0);
        assertNotNull(luffy.getNicknames());
        assertNotNull(luffy.getNameKanji());
        assertNotNull(luffy.getAbout());
        assertNotNull(luffy.getImages());
    }

    @Test
    void testCharacterAppearance1() throws APIException, InvalidIdException {
        List<Appearance> luffy = JikanApiClient.getCharacter(40, MediaType.ANIME);
        assertNotNull(luffy);
        assertFalse(luffy.isEmpty());
        assertEquals("Main", luffy.getFirst().getRole());
    }

    @Test
    void testCharacterAppearance2() throws APIException, InvalidIdException {
        List<Appearance> kaidou = JikanApiClient.getCharacter(46109, MediaType.ANIME);

        assertNotNull(kaidou);
        assertFalse(kaidou.isEmpty());
        assertEquals("Supporting", kaidou.getFirst().getRole());
    }

    @Test
    void testCharacterSearch() throws APIException, InvalidRequestException {
        List<Character> luffy = JikanApiClient.searchCharacter("Luffy");

        assertNotNull(luffy);
        assertFalse(luffy.isEmpty());
    }
}