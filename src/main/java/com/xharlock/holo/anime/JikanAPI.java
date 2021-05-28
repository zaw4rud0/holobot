package com.xharlock.holo.anime;

import java.io.IOException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class JikanAPI {
	private static final String baseUrl = "https://api.jikan.moe/v3";
	private static int limit = 10;

	public static JsonArray search(String name, String type) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append(baseUrl + "/search/" + type + "/")
				.append("?q=").append(name.replaceAll(" ", "%20"))
				.append("&limit=").append(limit);
		return getJsonObject(builder.toString()).getAsJsonArray("results");
	}

	public static JsonObject getAnime(int id) throws IOException {
		String url = baseUrl + "/anime/" + id;
		return getJsonObject(url);
	}

	public static JsonObject getManga(int id) throws IOException {
		String url = baseUrl + "/manga/" + id;
		return getJsonObject(url);
	}

	private static JsonObject getJsonObject(String urlQueryString) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestProperty("User-Agent",	"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		
		String redirect = connection.getHeaderField("Location");
		
		while (redirect != null) {
			connection = (HttpURLConnection) new URL(redirect).openConnection();
			redirect = connection.getHeaderField("Location");
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		
		return JsonParser.parseString(s).getAsJsonObject();
	}
}