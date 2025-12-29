package dev.zawarudo.holo.modules;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MerriamWebsterClientIT {

    private static MerriamWebsterClient client;

    @BeforeAll
    static void setup() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String dictionaryKey = dotenv.get("KEY_DICTIONARY");
        String thesaurusKey = dotenv.get("KEY_THESAURUS");

        Assumptions.assumeTrue(
                dictionaryKey != null && !dictionaryKey.isBlank(),
                "KEY_DICTIONARY not set – skipping Merriam-Webster tests"
        );

        Assumptions.assumeTrue(
                thesaurusKey != null && !thesaurusKey.isBlank(),
                "KEY_THESAURUS not set – skipping Merriam-Webster tests"
        );

        client = new MerriamWebsterClient(dictionaryKey, thesaurusKey);
    }

    @Test
    void dictionaryLookup() {
        var result = client.lookupDictionary("car");

        assertNotNull(result);
        assertTrue(result.hasEntries() || result.hasSuggestions(),
                "Expected dictionary entries or suggestions"
        );
    }

    @Test
    void thesaurusLookup() {
        var result = client.lookupThesaurus("happy");

        assertNotNull(result);
        assertTrue(result.hasEntries() || result.hasSuggestions(),
                "Expected thesaurus entries or suggestions"
        );
    }
}