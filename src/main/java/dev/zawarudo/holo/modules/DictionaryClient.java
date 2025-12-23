package dev.zawarudo.holo.modules;

import com.google.gson.JsonArray;
import dev.zawarudo.holo.utils.HoloHttp;
import dev.zawarudo.holo.utils.exceptions.HttpStatusException;
import dev.zawarudo.holo.utils.exceptions.HttpTransportException;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public final class DictionaryClient {

    private static final String DICTIONARY_URL = "https://www.dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s";
    private static final String THESAURUS_URL = "https://www.dictionaryapi.com/api/v3/references/thesaurus/json/%s?key=%s";

    private DictionaryClient() {
        throw new UnsupportedOperationException();
    }

    /**
     * Fetches the definition of a word from the Dictionary API.
     *
     * @param word the word to look up
     * @param apiKey the API key for authentication
     * @return the JSON response from the API as a string
     * @throws IOException if an I/O error occurs
     */
    public static String getDefinition(String word, String apiKey) throws HttpStatusException, HttpTransportException {
        String urlString = String.format(DICTIONARY_URL, word, apiKey);

        System.out.println(urlString);

        JsonArray array = HoloHttp.getJsonArray(urlString);

        return null;
    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String keyDictionary = dotenv.get("KEY_DICTIONARY");
        String keyThesaurus = dotenv.get("KEY_THESAURUS");

        try {
            getDefinition("Car", keyDictionary);
        } catch (HttpStatusException e) {
            throw new RuntimeException(e);
        } catch (HttpTransportException e) {
            throw new RuntimeException(e);
        }

    }
}
