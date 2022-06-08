package dev.zawarudo.holo.apis;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.exceptions.InvalidRequestException;
import dev.zawarudo.holo.utils.HttpResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for the Dog CEO API.
 */
public final class DogAPI {

    private DogAPI() {
    }

    /**
     * Returns a random image url of a dog.
     */
    public static String getRandomImage() throws APIException {
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(ENDPOINT.RANDOM.getUrl());
        } catch (IOException e) {
            throw new APIException();
        }
        return obj.get("message").getAsString();
    }

    /**
     * Returns a random image url of a specified dog breed.
     */
    public static String getRandomBreedImage(String breed) throws APIException, InvalidRequestException {
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(String.format(ENDPOINT.RANDOM_BREED.getUrl(), breed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed: " + breed);
            }
            throw new APIException();
        }
        return obj.get("message").getAsString();
    }

    /**
     * Returns a list of image urls of a specified breed.
     */
    public static List<String> getBreedImages(String breed) throws APIException, InvalidRequestException {
        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(String.format(ENDPOINT.BY_BREED.getUrl(), breed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed: " + breed);
            }
            throw new APIException();
        }
        JsonArray array = jsonObj.getAsJsonArray("message");
        Type listType = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(array, listType);
    }

    /**
     * Returns a random image url of a specified sub-breed.
     */
    public static String getRandomSubBreedImage(String breed, String subBreed) throws APIException, InvalidRequestException {
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(String.format(ENDPOINT.RANDOM_SUB_BREED.getUrl(), breed, subBreed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed or sub-breed: " + breed + ", " + subBreed);
            }
            throw new APIException();
        }
        return obj.get("message").getAsString();
    }

    /**
     * Returns a list of image urls of a specified sub-breed.
     */
    public static List<String> getSubBreedImages(String breed, String subBreed) throws APIException, InvalidRequestException {
        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(String.format(ENDPOINT.BY_SUB_BREED.getUrl(), breed, subBreed));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed or sub-breed: " + breed + ", " + subBreed);
            }
            throw new APIException();
        }
        JsonArray array = jsonObj.getAsJsonArray("message");
        Type listType = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(array, listType);
    }

    /**
     * Returns a list of breeds from the Dog CEO API.
     */
    public static List<Breed> getBreedList() throws APIException {
        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(ENDPOINT.ALL_BREEDS.getUrl());
        } catch (IOException e) {
            throw new APIException();
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
     * Returns a list of sub-breeds of a specified breed.
     */
    public static List<String> getSubBreeds(String breed) throws InvalidRequestException, APIException {
        String url = String.format(ENDPOINT.ALL_SUB_BREEDS.getUrl(), breed);

        JsonObject jsonObj;
        try {
            jsonObj = HttpResponse.getJsonObject(url);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid breed: " + breed);
            }
            throw new APIException();
        }

        // Check if request was successful
        if (!jsonObj.get("status").getAsString().equals("success")) {
            throw new InvalidRequestException("Invalid breed: " + breed);
        }

        JsonArray array = jsonObj.getAsJsonArray("message");
        Type listType = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(array, listType);
    }

    /**
     * A record representing a breed and its sub-breeds.
     */
    public record Breed(String name, String[] subBreeds) {
        /**
         * Checks if this breed has sub-breeds.
         * @return true if this breed has sub-breeds, false otherwise.
         */
        public boolean hasSubBreeds() {
            return subBreeds != null && subBreeds.length > 0;
        }
    }

    /**
     * Represents an endpoint of the API.
     */
    private enum ENDPOINT {
        ALL_BREEDS("/breeds/list/all"),
        ALL_SUB_BREEDS("/breed/%s/list"),
        RANDOM("/breeds/image/random"),
        BY_BREED("/breed/%s/images"),
        RANDOM_BREED("/breed/%s/images/random"),
        BY_SUB_BREED("/breed/%s/%s/images"),
        RANDOM_SUB_BREED("/breed/%s/%s/images/random");

        private static final String BASE_URL = "https://dog.ceo/api";
        private final String endpoint;

        ENDPOINT(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Returns the full URL of this endpoint.
         * @return the URL of the endpoint.
         */
        public String getUrl() {
            return BASE_URL + endpoint;
        }
    }
}