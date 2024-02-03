package dev.zawarudo.holo.utils;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Utility class for sending HTTP requests and reading the responses.
 */
@SuppressWarnings("UnstableApiUsage")
public final class HttpResponse {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(1);

    private HttpResponse() {
    }

    /**
     * Sends a request to the specified URL and returns the response body as a String.
     *
     * @param url The URL to send the HTTP request to.
     * @return The response body as a String.
     */
    public static String sendHttpRequest(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Sends a request to the given URL and returns the response body as a JsonObject.
     *
     * @param url The URL to send the HTTP request to.
     * @return The response body as a JsonObject.
     */
    public static JsonObject getJsonObject(String url) throws IOException {
        String response = sendHttpRequest(url);
        return JsonParser.parseString(response).getAsJsonObject();
    }

    /**
     * Sends a request to the given URL and returns the response body as a JsonArray.
     *
     * @param url The URL to send the HTTP request to.
     * @return The response body as a JsonArray.
     */
    public static JsonArray getJsonArray(String url) throws IOException {
        String response = sendHttpRequest(url);
        return JsonParser.parseString(response).getAsJsonArray();
    }

    /**
     * Sends a request to the specified URL and returns the first line of the response body as a String.
     *
     * @param url The URL to send the HTTP request to.
     * @return The first line of the response body as a String.
     */
    public static String readLine(String url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()))) {
            return reader.readLine();
        }
    }

    // TODO: Proper integration and code clean-up
    @Nullable
    public static JsonObject getJsonObject2(String url) throws InvalidRequestException, APIException {
        String s;
        RATE_LIMITER.acquire();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

            // Follow redirects
            String redirect = connection.getHeaderField("Location");
            while (redirect != null) {
                connection = (HttpURLConnection) new URL(redirect).openConnection();
                redirect = connection.getHeaderField("Location");
            }

            // Check the response code
            switch (connection.getResponseCode()) {
                case 200 -> {} // OK
                case 400 -> throw new InvalidRequestException("400: Invalid Request");
                case 404 -> throw new InvalidRequestException("404: Not Found");
                case 405 -> throw new InvalidRequestException("405: Method Not Allowed");
                case 429 -> throw new InvalidRequestException("429: Too Many Requests");
                case 500 -> throw new APIException("500: Internal Server Error");
                case 503 -> throw new APIException("503: Service Unavailable");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            s = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return JsonParser.parseString(s).getAsJsonObject();
    }
}