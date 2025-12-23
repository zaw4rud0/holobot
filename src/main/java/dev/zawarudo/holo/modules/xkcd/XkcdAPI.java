package dev.zawarudo.holo.modules.xkcd;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.utils.HoloHttp;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.HttpStatusException;
import dev.zawarudo.holo.utils.exceptions.HttpTransportException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;

public final class XkcdAPI {

    private static final Gson GSON = new Gson();

    private XkcdAPI() {
    }

    /**
     * Fetches the xkcd comic with the given number.
     */
    public static XkcdComic getComic(int num) throws APIException, InvalidRequestException {
        String url = String.format(Endpoint.BY_NUMBER.getUrl(), num);
        try {
            JsonObject obj = fetchJsonOrThrow(url);
            return GSON.fromJson(obj, XkcdComic.class);
        } catch (InvalidRequestException e) {
            throw new InvalidRequestException("Invalid issue number: " + num, e);
        }
    }

    /**
     * Fetches the latest comic of xkcd.
     */
    public static XkcdComic getLatest() throws APIException {
        String url = Endpoint.LATEST.getUrl();
        try {
            JsonObject obj = fetchJsonOrThrow(url);
            return GSON.fromJson(obj, XkcdComic.class);
        } catch (InvalidRequestException e) {
            // Should never happen
            throw new APIException("Unexpected client error while fetching latest xkcd.", e);
        }
    }

    private static JsonObject fetchJsonOrThrow(String url) throws APIException, InvalidRequestException {
        try {
            return HoloHttp.getJsonObject(url);
        } catch (HttpStatusException e) {
            int code = e.getStatusCode();

            // xkcd: non-existing comic -> 404
            if (code == 404) {
                throw new InvalidRequestException("Not found: " + url, e);
            }

            // other 4xx -> still "invalid request"
            if (code >= 400 && code < 500) {
                throw new InvalidRequestException("Invalid request (" + code + "): " + url, e);
            }

            // 5xx etc
            throw new APIException("Remote API error (" + code + "): " + url, e);
        } catch (HttpTransportException e) {
            throw new APIException("I/O error while contacting xkcd: " + url, e);
        }
    }

    private enum Endpoint {
        LATEST("info.0.json"),
        BY_NUMBER("%s/info.0.json");

        private static final String BASE_URL = "https://xkcd.com/";
        private final String suffix;

        Endpoint(String suffix) {
            this.suffix = suffix;
        }

        public String getUrl() {
            return BASE_URL + suffix;
        }
    }
}