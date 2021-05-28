package com.xharlock.holo.image;

import java.io.IOException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;
import java.net.HttpURLConnection;

import com.google.gson.JsonObject;

public class GelbooruAPI {

	private static final String baseUrl = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&";

	public static JsonObject getJsonObject(Rating rating, Sort sort, int limit, String tags) throws IOException {
		String urlQueryString = baseUrl + "limit=" + limit + "&tags=rating:" + rating.getName() + "%20sort:" + sort
				+ "%20" + tags;
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		return JsonParser.parseString(s).getAsJsonArray().get(0).getAsJsonObject();
	}
	
	public static JsonArray getJsonArray(Rating rating, Sort sort, int limit, String tags) throws IOException {
		String urlQueryString = baseUrl + "limit=" + limit + "&tags=rating:" + rating.getName() + "%20sort:" + sort
				+ "%20" + tags;		
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		return JsonParser.parseString(s).getAsJsonArray();
	}

	public enum Rating {
		SAFE("safe%20-rating:questionable%20-rating:explicit"), QUESTIONABLE("questionable"), EXPLICIT("explicit");

		private String name;

		Rating(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public enum Sort {
		RANDOM("random"), SCORE_ASC("score:asc"), SCORE_DESC("score:desc"), UPDATED_ASC("updated:asc"),
		UPDATED_DESC("updated:desc");

		private String name;

		Sort(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
