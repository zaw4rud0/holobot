package dev.zawarudo.holo.modules;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.utils.HoloHttp;
import dev.zawarudo.holo.utils.exceptions.*;
import dev.zawarudo.holo.utils.TypeTokenUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for the <a href="https://dog.ceo/dog-api/">Dog CEO API</a>.
 */
public final class DogCeoClient {

    private static final Gson GSON = new Gson();

    private DogCeoClient() {
    }

    /**
     * Fetches a random image url of a dog.
     *
     * @return An image URL of a dog as a String.
     * @throws APIException If the API returns an error.
     */
    @NotNull
    public static String getRandomImage() throws APIException {
        String url = ENDPOINT.RANDOM.getUrl();
        try {
            JsonObject obj = fetchJsonOrThrow(ENDPOINT.RANDOM.getUrl());
            return obj.get("message").getAsString();
        } catch (InvalidRequestException ex) {
            // This should not happen
            throw new APIException("Unexpected error response for endpoint with no input: " + url, ex);
        }
    }

    /**
     * Fetches a random image url of a specified dog breed.
     *
     * @param breed The breed of the dog.
     * @return An image URL of a dog of the given breed as a String.
     * @throws APIException            If the API returns an error.
     * @throws InvalidRequestException If the given breed is invalid.
     */
    @NotNull
    public static String getRandomBreedImage(@NotNull String breed) throws APIException, InvalidRequestException {
        String url = String.format(ENDPOINT.RANDOM_BREED.getUrl(), breed);
        JsonObject obj = fetchJsonOrThrow(url);
        return obj.get("message").getAsString();
    }

    /**
     * Fetches a list of image urls of a specified breed.
     *
     * @param breed The breed of the dog.
     * @return A list of image URLs of a dog of the given breed.
     * @throws APIException            If the API returns an error.
     * @throws InvalidRequestException If the given breed is invalid.
     */
    @NotNull
    public static List<String> getBreedImages(@NotNull String breed) throws APIException, InvalidRequestException {
        String url = String.format(ENDPOINT.BY_BREED.getUrl(), breed);
        JsonObject obj = fetchJsonOrThrow(url);
        return parseMessageAsStringList(obj);
    }

    /**
     * Fetches a random image url of a specified sub-breed.
     *
     * @param breed    The breed of the dog.
     * @param subBreed The sub-breed of the dog.
     * @return An image URL of a dog of the given sub-breed as a String.
     * @throws APIException            If the API returns an error.
     * @throws InvalidRequestException If the given breed or sub-breed is invalid.
     */
    @NotNull
    public static String getRandomSubBreedImage(@NotNull String breed, @NotNull String subBreed) throws APIException, InvalidRequestException {
        String url = String.format(ENDPOINT.RANDOM_SUB_BREED.getUrl(), breed, subBreed);
        JsonObject obj = fetchJsonOrThrow(url);
        return obj.get("message").getAsString();
    }

    /**
     * Fetches a list of image urls of a specified sub-breed.
     *
     * @param breed    The breed of the dog.
     * @param subBreed The sub-breed of the dog.
     * @return A list of image URLs of a dog of the given sub-breed.
     * @throws APIException            If the API returns an error.
     * @throws InvalidRequestException If the given breed or sub-breed is invalid.
     */
    @NotNull
    public static List<String> getSubBreedImages(@NotNull String breed, @NotNull String subBreed) throws APIException, InvalidRequestException {
        String url = String.format(ENDPOINT.BY_SUB_BREED.getUrl(), breed, subBreed);
        JsonObject obj = fetchJsonOrThrow(url);
        return parseMessageAsStringList(obj);
    }

    /**
     * Fetches a list of breeds from the Dog CEO API.
     *
     * @return A list of breeds.
     * @throws APIException If the API returns an error.
     */
    @NotNull
    public static List<Breed> getBreedList() throws APIException {
        String url = ENDPOINT.ALL_BREEDS.getUrl();

        JsonObject obj;
        try {
            obj = fetchJsonOrThrow(url);
        } catch (InvalidRequestException ex) {
            // This should not happen
            throw new APIException("Unexpected error response for endpoint with no input: " + url, ex);
        }

        JsonObject breeds = obj.getAsJsonObject("message");

        List<Breed> breedList = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : breeds.entrySet()) {
            String[] subBreeds = GSON.fromJson(entry.getValue().getAsJsonArray(), String[].class);
            breedList.add(new Breed(entry.getKey(), subBreeds));
        }
        return breedList;
    }

    /**
     * Fetches a list of sub-breeds of a specified breed.
     *
     * @param breed The breed of the dog.
     * @return A list of sub-breeds of the given breed.
     * @throws APIException            If the API returns an error.
     * @throws InvalidRequestException If the given breed is invalid.
     */
    @NotNull
    public static List<String> getSubBreeds(@NotNull String breed) throws APIException, InvalidRequestException {
        String url = String.format(ENDPOINT.ALL_SUB_BREEDS.getUrl(), breed);
        JsonObject obj = fetchJsonOrThrow(url);
        return parseMessageAsStringList(obj);
    }

    private static List<String> parseMessageAsStringList(JsonObject obj) {
        JsonArray array = obj.getAsJsonArray("message");
        Type listType = TypeTokenUtils.getListTypeToken(String.class);
        return GSON.fromJson(array, listType);
    }

    private static JsonObject fetchJsonOrThrow(String url) throws APIException, InvalidRequestException {
        try {
            JsonObject obj = HoloHttp.getJsonObject(url);
            if (obj.has("status") && !"success".equalsIgnoreCase(obj.get("status").getAsString())) {
                String msg = obj.has("message") ? obj.get("message").getAsString() : "Invalid request";
                throw new InvalidRequestException(msg);
            }
            return obj;
        } catch (HttpStatusException e) {
            int code = e.getStatusCode();
            if (code == 404) {
                throw new InvalidRequestException("Not found: " + url, e);
            }
            if (code >= 400 && code < 500) {
                throw new InvalidRequestException("Invalid request (" + code + "): " + url, e);
            }
            throw new APIException("API error (" + code + "): " + url, e);
        } catch (HttpTransportException e) {
            throw new APIException("I/O error while contacting DogCEO: " + url, e);
        }
    }

    /**
     * A record representing a breed and its sub-breeds.
     *
     * @param name      The name of the breed.
     * @param subBreeds The sub-breeds of the given breed.
     */
    public record Breed(@NotNull String name, String[] subBreeds) {
        /**
         * Checks if this breed has sub-breeds.
         *
         * @return True if this breed has sub-breeds, false otherwise.
         */
        public boolean hasSubBreeds() {
            return subBreeds != null && subBreeds.length > 0;
        }

        @Override
        public String toString() {
            return "Breed{" + "name='" + name + '\'' + ", subBreeds=" + Arrays.toString(subBreeds) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return this.name.equals(((Breed) o).name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + Arrays.hashCode(subBreeds);
            return result;
        }
    }

    /**
     * Represents an endpoint of the API. Note that some endpoints require a breed, a sub-breed or both
     * to be specified. Those endpoints use format specifiers.
     */
    private enum ENDPOINT {
        /**
         * Endpoint for fetching a list of all available breeds.
         */
        ALL_BREEDS("/breeds/list/all"),
        /**
         * Endpoint for fetching a list of all available sub-breeds of a given breed.
         */
        ALL_SUB_BREEDS("/breed/%s/list"),
        /**
         * Endpoint for fetching a random image.
         */
        RANDOM("/breeds/image/random"),
        /**
         * Endpoint for fetching a list of images of a given breed.
         */
        BY_BREED("/breed/%s/images"),
        /**
         * Endpoint for fetching a random image of a given breed.
         */
        RANDOM_BREED("/breed/%s/images/random"),
        /**
         * Endpoint for fetching a list of images of a given sub-breed.
         */
        BY_SUB_BREED("/breed/%s/%s/images"),
        /**
         * Endpoint for fetching a random image of a given sub-breed.
         */
        RANDOM_SUB_BREED("/breed/%s/%s/images/random");

        private static final String BASE_URL = "https://dog.ceo/api";
        private final String endpointUrl;

        ENDPOINT(String endpointUrl) {
            this.endpointUrl = endpointUrl;
        }

        /**
         * Gets the full URL of this endpoint.
         *
         * @return The URL of the endpoint as a String.
         */
        public @NotNull String getUrl() {
            return BASE_URL + endpointUrl;
        }
    }
}