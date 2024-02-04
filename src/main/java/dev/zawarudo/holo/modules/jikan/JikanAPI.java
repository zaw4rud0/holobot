package dev.zawarudo.holo.modules.jikan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.zawarudo.holo.modules.jikan.model.*;
import dev.zawarudo.holo.modules.jikan.model.Character;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.MyRateLimiter;
import dev.zawarudo.holo.utils.TypeTokenUtils;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JikanAPI {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

    private static final Logger LOGGER = LoggerFactory.getLogger(JikanAPI.class);

    private static final String BASE_URL = "https://api.jikan.moe/v4";
    private static int limit = 10;

    private static final MyRateLimiter RATE_LIMITER = new MyRateLimiter(1);

    private JikanAPI() {
    }

    /**
     * Sets the limit of the results. By default, the limit is 10.
     *
     * @param limit The limit of the results.
     */
    public static void setLimit(int limit) {
        // Jikan has a configured max value of 25
        JikanAPI.limit = Math.min(25, limit);
    }

    /**
     * Search for an anime using its name.
     *
     * @param name The name of the anime to search for.
     * @return A {@link List} of {@link Anime} objects.
     */
    public static List<Anime> searchAnime(String name) throws InvalidRequestException, APIException {
        String url = BASE_URL + "/anime?q=" + Formatter.encodeUrl(name) + "&limit=" + limit;

        RATE_LIMITER.acquire();

        Optional<JsonObject> result = fetchJsonData(url);
        if (result.isEmpty()) {
            throw new APIException();
        }

        JsonArray array = result.get().getAsJsonArray("data");
        return new Gson().fromJson(array, TypeTokenUtils.getListTypeToken(Anime.class));
    }

    /**
     * Search for a manga using its name.
     *
     * @param name The name of the manga to search for.
     * @return A {@link List} of {@link Manga} objects.
     */
    public static List<Manga> searchManga(String name) throws InvalidRequestException, APIException {
        String url = BASE_URL + "/manga?q=" + Formatter.encodeUrl(name) + "&limit=" + limit;

        RATE_LIMITER.acquire();

        Optional<JsonObject> result = fetchJsonData(url);
        if (result.isEmpty()) {
            throw new APIException();
        }

        JsonArray array = result.get().getAsJsonArray("data");
        return new Gson().fromJson(array, TypeTokenUtils.getListTypeToken(Manga.class));
    }

    /**
     * Fetches an {@link Anime} object using the id from MyAnimeList.
     *
     * @param id MyAnimeList id of the anime.
     * @return An {@link Anime} object.
     */
    public static Anime getAnime(int id) throws InvalidIdException, APIException {
        try {
            String url = BASE_URL + "/anime/" + id;

            RATE_LIMITER.acquire();

            Optional<JsonObject> result = fetchJsonData(url);
            if (result.isEmpty()) {
                throw new APIException();
            }

            return new Gson().fromJson(result.get().getAsJsonObject("data"), Anime.class);
        } catch (InvalidRequestException e) {
            throw new InvalidIdException(e);
        }
    }

    /**
     * Fetches a {@link Manga} object using the id from MyAnimeList.
     *
     * @param id MyAnimeList id of the manga.
     * @return A {@link Manga} object.
     */
    public static Manga getManga(int id) throws InvalidIdException, APIException {
        try {
            String url = BASE_URL + "/manga/" + id;

            RATE_LIMITER.acquire();

            Optional<JsonObject> result = fetchJsonData(url);
            if (result.isEmpty()) {
                throw new APIException();
            }

            return new Gson().fromJson(result.get().getAsJsonObject("data"), Manga.class);
        } catch (InvalidRequestException e) {
            throw new InvalidIdException();
        }
    }

    /**
     * Fetches a list of the best anime according to MyAnimeList.
     *
     * @param limit The number of anime to retrieve.
     */
    public static List<Anime> getTopAnime(int limit) throws APIException {
        String url = BASE_URL + "/top/anime?page=";

        List<Anime> topAnime = new ArrayList<>();
        int count = 0;
        int page = 1;

        Type type = TypeTokenUtils.getListTypeToken(Anime.class);

        while (count < limit) {

            JsonObject json;
            try {
                RATE_LIMITER.acquire();
                Optional<JsonObject> result = fetchJsonData(url + page);
                if (result.isEmpty()) {
                    throw new APIException();
                }

                json = result.get();
            } catch (InvalidRequestException e) {
                throw new IllegalStateException("This wasn't supposed to happen!");
            }

            List<Anime> temp = new Gson().fromJson(json.getAsJsonArray("data"), type);

            if (count + temp.size() > limit) {
                int i = 0;
                while (count < limit) {
                    topAnime.add(temp.get(i));
                    count++;
                    i++;
                }
            } else {
                topAnime.addAll(temp);
                page++;
                count += temp.size();
            }
        }
        return topAnime;
    }

    /**
     * Fetches the relations of a given media.
     *
     * @param id   The id of the media.
     * @param type The type of the media.
     * @return A {@link List} of {@link Related} objects.
     */
    public static List<Related> getRelated(int id, MediaType type) throws APIException, InvalidRequestException {
        String url = BASE_URL + "/" + type.getType() + "/" + id + "/relations";

        RATE_LIMITER.acquire();

        Optional<JsonObject> result = fetchJsonData(url);
        if (result.isEmpty()) {
            throw new APIException();
        }

        JsonArray array = result.get().getAsJsonArray("data");
        return new Gson().fromJson(array, TypeTokenUtils.getListTypeToken(Related.class));
    }

    /**
     * Fetches a list of {@link Anime} of the current anime season.
     *
     * @return A {@link List} of {@link Anime} objects.
     */
    public static List<Anime> getSeason() throws APIException {
        String url = BASE_URL + "/seasons/now";
        return collectAnime(url);
    }

    /**
     * Fetches a list of {@link Anime} of the given anime season.
     *
     * @param season The season to get the anime from.
     * @param year   The year to get the anime from.
     */
    public static List<Anime> getSeason(Season season, int year) throws APIException {
        String url = BASE_URL + "/seasons/" + year + "/" + season;
        return collectAnime(url);
    }

    private static List<Anime> collectAnime(String url) throws APIException {
        JsonObject json;
        List<Anime> list = new ArrayList<>();

        Type type = TypeTokenUtils.getListTypeToken(Anime.class);

        int i = 1;
        do {
            try {
                RATE_LIMITER.acquire();
                Optional<JsonObject> result = fetchJsonData(url + "?page=" + i++);
                if (result.isEmpty()) {
                    throw new APIException();
                }

                json = result.get();
            } catch (InvalidRequestException e) {
                throw new IllegalStateException("This wasn't supposed to happen!");
            }
            list.addAll(new Gson().fromJson(json.getAsJsonArray("data"), type));
        } while (json.getAsJsonObject("pagination").get("has_next_page").getAsBoolean());
        return list;
    }

    /**
     * Searches for a {@link Character} using its name.
     *
     * @param name The name of the character to search for.
     * @return A {@link List} of {@link Character} objects.
     */
    public static List<Character> searchCharacter(String name) throws APIException, InvalidRequestException {
        String url = BASE_URL + "/characters?q=" + Formatter.encodeUrl(name) + "&order_by=favorites&sort=desc";

        RATE_LIMITER.acquire();

        Optional<JsonObject> result = fetchJsonData(url);
        if (result.isEmpty()) {
            throw new APIException();
        }

        JsonArray array = result.get().getAsJsonArray("data");
        return new Gson().fromJson(array, TypeTokenUtils.getListTypeToken(Character.class));
    }

    /**
     * Returns a character from a given id.
     *
     * @param id MyAnimeList id of the character. See also <a href="https://myanimelist.net/character.php">MyAnimeList</a>.
     * @return A {@link Character} object.
     */
    public static Character getCharacter(long id) throws InvalidIdException, APIException {
        try {
            String url = BASE_URL + "/characters/" + id;

            RATE_LIMITER.acquire();

            Optional<JsonObject> result = fetchJsonData(url);
            if (result.isEmpty()) {
                throw new APIException();
            }

            return new Gson().fromJson(result.get().getAsJsonObject("data"), Character.class);
        } catch (InvalidRequestException e) {
            throw new InvalidIdException();
        }
    }

    /**
     * Fetches the anime or manga in which a specified character had an appearance.
     *
     * @param id   MyAnimeList id of the character
     * @param type Anime or manga. See also {@link MediaType}
     * @return A {@link List} of {@link Appearance} objects.
     */
    public static List<Appearance> getCharacter(long id, MediaType type) throws APIException, InvalidIdException {
        try {
            String url = BASE_URL + "/characters/" + id + "/" + type.getType();

            RATE_LIMITER.acquire();

            Optional<JsonObject> result = fetchJsonData(url);
            if (result.isEmpty()) {
                throw new APIException();
            }

            JsonArray array = result.get().getAsJsonArray("data");
            return new Gson().fromJson(array, TypeTokenUtils.getListTypeToken(Appearance.class));
        } catch (InvalidRequestException e) {
            throw new InvalidIdException();
        }
    }

    /**
     * Fetches a random {@link Anime} from MyAnimeList.
     *
     * @return A {@link Anime} object.
     */
    public static Anime getRandomAnime() throws APIException {
        try {
            String url = BASE_URL + "/random/anime";

            RATE_LIMITER.acquire();

            Optional<JsonObject> result = fetchJsonData(url);
            if (result.isEmpty()) {
                throw new APIException();
            } else {
                return new Gson().fromJson(result.get().getAsJsonObject("data"), Anime.class);
            }
        } catch (InvalidRequestException e) {
            throw new IllegalStateException("This wasn't supposed to happen!");
        }
    }

    /**
     * Fetches a random {@link Manga} from MyAnimeList.
     *
     * @return A {@link Manga} object.
     */
    public static Manga getRandomManga() throws APIException {
        try {
            String url = BASE_URL + "/random/manga";

            RATE_LIMITER.acquire();

            Optional<JsonObject> result = fetchJsonData(url);
            if (result.isEmpty()) {
                throw new APIException();
            } else {
                return new Gson().fromJson(result.get().getAsJsonObject("data"), Manga.class);
            }
        } catch (InvalidRequestException e) {
            throw new IllegalStateException("This wasn't supposed to happen!");
        }
    }

    // TODO: Extract this method and move it to HttpResponse
    private static Optional<JsonObject> fetchJsonData(String url) throws InvalidRequestException, APIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);

            // Follow redirects
            String redirect = connection.getHeaderField("Location");
            while (redirect != null) {
                connection = (HttpURLConnection) URI.create(redirect).toURL().openConnection();
                redirect = connection.getHeaderField("Location");
            }

            // Check the response code
            switch (connection.getResponseCode()) {
                case 400 -> throw new InvalidRequestException("400: Invalid Request");
                case 404 -> throw new InvalidRequestException("404: Not Found");
                case 405 -> throw new InvalidRequestException("405: Method Not Allowed");
                case 429 -> throw new InvalidRequestException("429: Too Many Requests");
                case 500 -> throw new APIException("500: Internal Server Error");
                case 503 -> throw new APIException("503: Service Unavailable");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonString = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            connection.disconnect();

            return Optional.of(JsonParser.parseString(jsonString).getAsJsonObject());
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("An error occurred while fetching the json data!", e);
            }
            return Optional.empty();
        }
    }
}