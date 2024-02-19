package dev.zawarudo.holo.modules.aoc.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class AdventOfCodeAPI {

    private static int year;

    private AdventOfCodeAPI() {
    }

    public static List<AdventDay> getAdventDays(int year, int leaderboardId, String sessionKey) {
        AdventOfCodeAPI.year = year;

        JsonObject members = fetchJson(year, leaderboardId, sessionKey).get("members").getAsJsonObject();
        List<JsonObject> completionRates = new ArrayList<>();

        for (String key : members.keySet()) {
            JsonObject completion = members.get(key).getAsJsonObject().get("completion_day_level").getAsJsonObject();
            completionRates.add(completion);
        }
        return parseData(completionRates);
    }

    private static JsonObject fetchJson(int year, int leaderboardId, String sessionKey) {
        String url = String.format("https://adventofcode.com/%d/leaderboard/private/view/%d.json", year, leaderboardId);

        CookieHandler.setDefault(new CookieManager());
        HttpCookie cookie = new HttpCookie("session", sessionKey);
        cookie.setPath("/");
        cookie.setVersion(0);

        String body = "{\"Message\": \"Error\"}";

        try {
            ((CookieManager) CookieHandler.getDefault()).getCookieStore().add(new URI("https://adventofcode.com"), cookie);
            HttpClient client = HttpClient.newBuilder().cookieHandler(CookieHandler.getDefault()).connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().setHeader("Content-Type", "application/json").build();
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            body = response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return JsonParser.parseString(body).getAsJsonObject();
    }

    private static List<AdventDay> parseData(List<JsonObject> dataList) {
        List<AdventDay> days = new LinkedList<>();

        int maxDay = 25;

        // DateTime of Switzerland
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("Europe/Zurich"));

        if (dateTime.getYear() == year && dateTime.getMonthValue() == 12) {
            maxDay = Math.min(maxDay, dateTime.getDayOfMonth());
        }

        for (int i = 1; i <= maxDay; i++) {
            String day = String.valueOf(i);

            int gold = 0;
            int silver = 0;
            int gray = 0;

            for (JsonObject obj : dataList) {
                if (!obj.has(day)) {
                    gray++;
                } else if (obj.get(day).getAsJsonObject().size() == 1) {
                    silver++;
                } else {
                    gold++;
                }
            }
            days.add(new AdventDay(i, gold, silver, gray));
        }
        return days;
    }
}