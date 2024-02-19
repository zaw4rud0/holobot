package dev.zawarudo.holo.modules.aoc.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.zawarudo.holo.utils.exceptions.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(AdventOfCodeAPI.class);
    private static int year;

    private AdventOfCodeAPI() {
    }

    public static List<AdventDay> getAdventDays(int year, int leaderboardId, String sessionKey) throws APIException {
        AdventOfCodeAPI.year = year;

        JsonObject members = fetchJson(year, leaderboardId, sessionKey).get("members").getAsJsonObject();
        List<JsonObject> completionRates = new ArrayList<>();

        for (String key : members.keySet()) {
            JsonObject completion = members.get(key).getAsJsonObject().get("completion_day_level").getAsJsonObject();
            completionRates.add(completion);
        }
        return parseData(completionRates);
    }

    private static JsonObject fetchJson(int year, int leaderboardId, String sessionKey) throws APIException {
        String url = String.format("https://adventofcode.com/%d/leaderboard/private/view/%d.json", year, leaderboardId);

        CookieHandler.setDefault(new CookieManager());
        HttpCookie cookie = new HttpCookie("session", sessionKey);
        cookie.setPath("/");
        cookie.setVersion(0);

        String body;
        try {
            ((CookieManager) CookieHandler.getDefault()).getCookieStore().add(new URI("https://adventofcode.com"), cookie);
            HttpClient client = HttpClient.newBuilder().cookieHandler(CookieHandler.getDefault()).connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().setHeader("Content-Type", "application/json").build();
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            body = response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            LOGGER.error("An error occurred while fetching the AOC data.", e);
            throw new APIException(e);
        }

        return JsonParser.parseString(body).getAsJsonObject();
    }

    private static List<AdventDay> parseData(List<JsonObject> dataList) {
        List<AdventDay> days = new LinkedList<>();
        int maxDay = getMaxDay(year);

        for (int dayIndex = 1; dayIndex <= maxDay; dayIndex++) {
            String day = String.valueOf(dayIndex);
            int goldCount = 0;
            int silverCount = 0;
            int grayCount = 0;

            for (JsonObject obj : dataList) {
                if (!obj.has(day)) {
                    grayCount++;
                } else if (obj.get(day).getAsJsonObject().size() == 1) {
                    silverCount++;
                } else {
                    goldCount++;
                }
            }
            days.add(new AdventDay(dayIndex, goldCount, silverCount, grayCount));
        }
        return days;
    }

    private static int getMaxDay(int year) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("Europe/Zurich"));
        if (currentDateTime.getYear() == year && currentDateTime.getMonthValue() == 12) {
            return Math.min(25, currentDateTime.getDayOfMonth());
        }
        return 25;
    }
}