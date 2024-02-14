package dev.zawarudo.holo.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.stream.Collectors;

/**
 * Utility class for sending HTTP requests and reading the responses.
 */
public final class HttpResponse {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

    private HttpResponse() {
    }

    /**
     * Sends a request to the specified URL and returns the response body as a String.
     *
     * @param url The URL to send the HTTP request to.
     * @return The response body as a String.
     */
    public static String sendHttpRequest(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(URI.create(url).toURL().openConnection().getInputStream()))) {
            return reader.readLine();
        }
    }
}