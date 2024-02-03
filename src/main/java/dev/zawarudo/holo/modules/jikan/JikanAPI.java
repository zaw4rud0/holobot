package dev.zawarudo.holo.modules.jikan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.zawarudo.holo.modules.jikan.model.*;
import dev.zawarudo.holo.modules.jikan.model.Character;
import dev.zawarudo.holo.utils.HttpResponse;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class JikanAPI {

    private static final String BASE_URL = "https://api.jikan.moe/v4";
    private static int limit = 10;

    private JikanAPI() {
    }

    /**
     * Sets the limit of the results. By default, the limit is 10.
     *
     * @param limit The limit of the results.
     */
    public static void setLimit(int limit) {
        JikanAPI.limit = limit;
    }

    /**
     * Search for an anime using its name.
     *
     * @param name The name of the anime to search for.
     * @return A {@link List} of {@link Anime} objects.
     */
    public static List<Anime> searchAnime(String name) throws InvalidRequestException, APIException {
        String url = BASE_URL + "/anime?q=" + encode(name) + "&limit=" + limit;
        JsonObject obj = HttpResponse.getJsonObject2(url);
        if (obj == null) {
            throw new APIException();
        }
        JsonArray array = obj.getAsJsonArray("data");
        return new Gson().fromJson(array, new TypeToken<List<Anime>>() {}.getType());
    }

    /**
     * Search for a manga using its name.
     *
     * @param name The name of the manga to search for.
     * @return A {@link List} of {@link Manga} objects.
     */
    public static List<Manga> searchManga(String name) throws InvalidRequestException, APIException {
        String url = BASE_URL + "/manga?q=" + encode(name) + "&limit=" + limit;
        JsonObject obj = HttpResponse.getJsonObject2(url);
        if (obj == null) {
            throw new APIException();
        }
        JsonArray array = obj.getAsJsonArray("data");
        return new Gson().fromJson(array, new TypeToken<List<Manga>>() {
        }.getType());
    }

    /**
     * Get an {@link Anime} object using the id from MyAnimeList.
     *
     * @param id MyAnimeList id of the anime.
     * @return An {@link Anime} object.
     */
    public static Anime getAnime(int id) throws InvalidIdException, APIException {
        String url = BASE_URL + "/anime/" + id;
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject2(url);
        } catch (InvalidRequestException e) {
            throw new InvalidIdException(e.getMessage());
        }
        if (obj == null) {
            throw new APIException();
        }
        return new Gson().fromJson(obj.getAsJsonObject("data"), Anime.class);
    }

    /**
     * Returns a list of the best animes according to MyAnimeList.
     *
     * @param limit The number of animes to retrieve.
     */
    public static List<Anime> getTopAnimes(int limit) throws APIException {
        String url = BASE_URL + "/top/anime?page=";

        List<Anime> result = new ArrayList<>();
        int count = 0;
        int page = 1;

        Type type = new TypeToken<List<Anime>>() {}.getType();

        while (count < limit) {

            JsonObject obj;
            try {
                obj = HttpResponse.getJsonObject2(url + page);
            } catch (InvalidRequestException e) {
                throw new IllegalStateException("This wasn't supposed to happen!");
            }

            if (obj == null) {
                throw new APIException();
            }

            List<Anime> temp = new Gson().fromJson(obj.getAsJsonArray("data"), type);

            if (count + temp.size() > limit) {
                int i = 0;
                while (count < limit) {
                    result.add(temp.get(i));
                    count++;
                    i++;
                }
            } else {
                result.addAll(temp);
                page++;
                count += temp.size();
            }
        }
        return result;
    }

    /**
     * Get a {@link Manga} object using the id from MyAnimeList.
     *
     * @param id MyAnimeList id of the manga.
     * @return A {@link Manga} object.
     */
    public static Manga getManga(int id) throws InvalidIdException, APIException {
        String url = BASE_URL + "/manga/" + id;
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject2(url);
        } catch (InvalidRequestException e) {
            throw new InvalidIdException();
        }
        if (obj == null) {
            throw new APIException();
        }
        return new Gson().fromJson(obj.getAsJsonObject("data"), Manga.class);
    }

    /**
     * Returns the relations of a given media.
     *
     * @param id   The id of the media.
     * @param type The type of the media.
     * @return A {@link List} of {@link Related} objects.
     */
    public static List<Related> getRelated(int id, MediaType type) throws APIException, InvalidRequestException {
        String url = BASE_URL + "/" + type.getType() + "/" + id + "/relations";
        JsonObject obj = HttpResponse.getJsonObject2(url);
        if (obj == null) {
            throw new APIException();
        }
        JsonArray array = obj.getAsJsonArray("data");
        return new Gson().fromJson(array, new TypeToken<List<Related>>() {
        }.getType());
    }

    /**
     * Returns a list of {@link Anime}s of the current anime season.
     *
     * @return A {@link List} of {@link Anime} objects.
     */
    public static List<Anime> getSeason() throws APIException {
        String url = BASE_URL + "/seasons/now";
        return collectAnime(url);
    }

    /**
     * Returns a list of {@link Anime}s of the given anime season.
     *
     * @param season The season to get the anime from.
     * @param year   The year to get the anime from.
     */
    public static List<Anime> getSeason(Season season, int year) throws APIException {
        String url = BASE_URL + "/seasons/" + year + "/" + season;
        return collectAnime(url);
    }

    private static List<Anime> collectAnime(String url) throws APIException {
        JsonObject obj;
        List<Anime> list = new ArrayList<>();

        Type type = new TypeToken<List<Anime>>() {}.getType();

        int i = 1;
        do {
            try {
                obj = HttpResponse.getJsonObject2(url + "?page=" + i++);
            } catch (InvalidRequestException e) {
                throw new IllegalStateException("This wasn't supposed to happen!");
            }
            if (obj == null) {
                throw new APIException();
            }
            list.addAll(new Gson().fromJson(obj.getAsJsonArray("data"), type));
        } while (obj.getAsJsonObject("pagination").get("has_next_page").getAsBoolean());
        return list;
    }

    /**
     * Searches for a {@link Character} using its name.
     *
     * @param name The name of the character to search for.
     * @return A {@link List} of {@link Character} objects.
     */
    public static List<Character> searchCharacter(String name) throws APIException, InvalidRequestException {
        String url = BASE_URL + "/characters?q=" + encode(name) + "&order_by=favorites&sort=desc";
        JsonObject obj = HttpResponse.getJsonObject2(url);
        if (obj == null) {
            throw new APIException();
        }
        JsonArray array = obj.getAsJsonArray("data");
        return new Gson().fromJson(array, new TypeToken<List<Character>>() {
        }.getType());
    }

    /**
     * Returns a character from a given id.
     *
     * @param id MyAnimeList id of the character. See also <a href="https://myanimelist.net/character.php">MyAnimeList</a>.
     * @return A {@link Character} object.
     */
    public static Character getCharacter(long id) throws APIException, InvalidIdException {
        String url = BASE_URL + "/characters/" + id;
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject2(url);
        } catch (InvalidRequestException e) {
            throw new InvalidIdException();
        }
        if (obj == null) {
            throw new APIException();
        }
        return new Gson().fromJson(obj.getAsJsonObject("data"), Character.class);
    }

    /**
     * Returns the appearance of a character in an anime or manga.
     *
     * @param id   MyAnimeList id of the character
     * @param type Anime or manga. See also {@link MediaType}
     * @return A {@link List} of {@link Appearance} objects.
     */
    public static List<Appearance> getCharacter(long id, MediaType type) throws APIException, InvalidIdException {
        String url = BASE_URL + "/characters/" + id + "/" + type.getType();
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject2(url);
        } catch (InvalidRequestException e) {
            throw new InvalidIdException();
        }
        if (obj == null) {
            throw new APIException();
        }
        JsonArray array = obj.getAsJsonArray("data");
        return new Gson().fromJson(array, new TypeToken<List<Appearance>>() {
        }.getType());
    }

    /**
     * Returns a random {@link Anime}.
     *
     * @return A {@link Anime} object.
     */
    public static Anime getRandomAnime() throws APIException {
        String url = BASE_URL + "/random/anime";
        JsonObject obj;

        try {
            obj = HttpResponse.getJsonObject2(url);
        } catch (InvalidRequestException e) {
            throw new IllegalStateException("This wasn't supposed to happen!");
        }
        if (obj == null) {
            throw new APIException();
        }
        return new Gson().fromJson(obj.getAsJsonObject("data"), Anime.class);
    }

    /**
     * Returns a random {@link Manga}.
     *
     * @return A {@link Manga} object.
     */
    public static Manga getRandomManga() throws APIException {
        String url = BASE_URL + "/random/manga";
        JsonObject obj;

        try {
            obj = HttpResponse.getJsonObject2(url);
        } catch (InvalidRequestException e) {
            throw new IllegalStateException("This wasn't supposed to happen!");
        }
        if (obj == null) {
            throw new APIException();
        }
        return new Gson().fromJson(obj.getAsJsonObject("data"), Manga.class);
    }

    /**
     * Encodes a given String using the ASCII character set to ensure a working URL.
     */
    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.US_ASCII).toLowerCase(Locale.UK);
    }
}