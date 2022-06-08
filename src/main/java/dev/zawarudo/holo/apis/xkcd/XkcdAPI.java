package dev.zawarudo.holo.apis.xkcd;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.exceptions.InvalidRequestException;
import dev.zawarudo.holo.utils.HttpResponse;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class XkcdAPI {

    private XkcdAPI() {
    }

    /**
     * Fetches the xkcd comic with the given number.
     */
    public static XkcdComic getComic(int num) throws APIException, InvalidRequestException {
        String url = String.format(Endpoint.BY_NUMBER.getUrl(), num);
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(url);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid issue number: " + num);
            }
            throw new APIException("Something went wrong with the API.");
        }
        return new Gson().fromJson(obj, XkcdComic.class);
    }

    /**
     * Fetches the latest comic of xkcd.
     */
    public static XkcdComic getLatest() throws APIException {
        String url = Endpoint.LATEST.getUrl();
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(url);
        } catch (IOException e) {
            throw new APIException("Something went wrong with the API.");
        }
        return new Gson().fromJson(obj, XkcdComic.class);
    }

    private enum Endpoint {
        LATEST("info.0.json"),
        BY_NUMBER("%s/info.0.json");

        private static final String BASE_URL = "https://xkcd.com/";
        private final String endpoint;

        Endpoint(String endpoint){
            this.endpoint = endpoint;
        }

        public String getUrl() {
            return BASE_URL + endpoint;
        }
    }
}