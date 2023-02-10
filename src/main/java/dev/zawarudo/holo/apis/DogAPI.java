package dev.zawarudo.holo.apis;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.exceptions.InvalidRequestException;
import dev.zawarudo.holo.utils.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for the <a href="https://dog.ceo/dog-api/">Dog CEO API</a>.
 */
public final class DogAPI {

    private DogAPI() {
    }

    /**
     * Fetches a random image url of a dog.
     *
     * @return An image URL of a dog as a String.
     * @throws APIException If the API returns an error.
     */
    public static @NotNull String getRandomImage() throws APIException {
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(ENDPOINT.RANDOM.getUrl());
        } catch (IOException e) {
            throw new APIException(e);
        }
        return obj.get("message").getAsString();
    }

    /**
     * Fetches a random image url of a specified dog breed.
     *
     * @param breed The breed of the dog.
     * @return An image URL of a dog of the given breed as a String.
     * @throws APIException            If the API returns an error.
     * @throws InvalidRequestException If the given breed is invalid.
     */
    public static @NotNull String getRandomBreedImage(@NotNull String breed) throws APIException, InvalidRequestException {
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(String.format(ENDPOINT.RANDOM_BREED.getUrl(), breed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed: " + breed, e);
            }
            throw new APIException(e);
        }
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
    public static @NotNull List<String> getBreedImages(@NotNull String breed) throws APIException, InvalidRequestException {
        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(String.format(ENDPOINT.BY_BREED.getUrl(), breed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed: " + breed, e);
            }
            throw new APIException(e);
        }
        JsonArray array = jsonObj.getAsJsonArray("message");
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(array, listType);
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
    public static @NotNull String getRandomSubBreedImage(@NotNull String breed, @NotNull String subBreed) throws APIException, InvalidRequestException {
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(String.format(ENDPOINT.RANDOM_SUB_BREED.getUrl(), breed, subBreed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed or sub-breed: " + breed + ", " + subBreed, e);
            }
            throw new APIException(e);
        }
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
    public static @NotNull List<String> getSubBreedImages(@NotNull String breed, @NotNull String subBreed) throws APIException, InvalidRequestException {
        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(String.format(ENDPOINT.BY_SUB_BREED.getUrl(), breed, subBreed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed or sub-breed: " + breed + ", " + subBreed, e);
            }
            throw new APIException(e);
        }
        JsonArray array = jsonObj.getAsJsonArray("message");
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(array, listType);
    }

    /**
     * Fetches a list of breeds from the Dog CEO API.
     *
     * @return A list of breeds.
     * @throws APIException If the API returns an error.
     */
    public static @NotNull List<Breed> getBreedList() throws APIException {
        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(ENDPOINT.ALL_BREEDS.getUrl());
        } catch (IOException e) {
            throw new APIException(e);
        }
        JsonObject breeds = jsonObj.getAsJsonObject("message");
        List<Breed> breedList = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : breeds.entrySet()) {
            String[] subBreeds = new Gson().fromJson(entry.getValue().getAsJsonArray(), String[].class);
            Breed breed = new Breed(entry.getKey(), subBreeds);
            breedList.add(breed);
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
    public static @NotNull List<String> getSubBreeds(@NotNull String breed) throws InvalidRequestException, APIException {
        String url = String.format(ENDPOINT.ALL_SUB_BREEDS.getUrl(), breed);

        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(url);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed: " + breed, e);
            }
            throw new APIException(e);
        }

        // Check if request was successful
        if (!"success".equals(jsonObj.get("status").getAsString())) {
            throw new InvalidRequestException("Invalid breed: " + breed);
        }

        JsonArray array = jsonObj.getAsJsonArray("message");
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(array, listType);
    }

    /**
     * A record representing a breed and its sub-breeds.
     *
     * @param name      The name of the breed.
     * @param subBreeds The sub-breeds of the given breed.
     */
    public @NotNull record Breed(@NotNull String name, String[] subBreeds) {
        /**
         * Checks if this breed has sub-breeds.
         *
         * @return True if this breed has sub-breeds, false otherwise.
         */
        public boolean hasSubBreeds() {
            return subBreeds != null && subBreeds.length > 0;
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
        private final String endpoint;

        ENDPOINT(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Gets the full URL of this endpoint.
         *
         * @return The URL of the endpoint as a String.
         */
        public @NotNull String getUrl() {
            return BASE_URL + endpoint;
        }
    }
}