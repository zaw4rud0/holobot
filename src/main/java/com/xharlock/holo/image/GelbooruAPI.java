package com.xharlock.holo.image;

import java.io.BufferedReader;
import java.io.IOException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.net.HttpURLConnection;

import com.google.gson.JsonObject;

public final class GelbooruAPI {

	private static final String baseUrl = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&";
	
	private GelbooruAPI() {
	}
	
	/** List of Gelbooru tags that are banned */ 
	private static final List<String> banned = new ArrayList<>();

	public static JsonObject getJsonObject(Rating rating, Sort sort, int limit,	String tags) throws IOException {		
		String urlQueryString = baseUrl + "limit=" + limit + "&tags=" + rating.getName() + sort.getName() + tags + "%20-" + String.join("%20-", banned);		
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		JsonArray array = JsonParser.parseString(s).getAsJsonArray();
		return array.get(0).getAsJsonObject();
	}

	public static JsonArray getJsonArray(Rating rating, Sort sort, int limit, String tags) throws IOException {
		String urlQueryString = baseUrl + "limit=" + limit + "&tags=" + rating.getName() + sort.getName() + tags; // + "%20-" + String.join("%20-", banned);
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		if (s.toString() == null || s.toString().equals("null") || s.toString().equals("")) {
			return null;
		}		
		return JsonParser.parseString(s).getAsJsonArray();
	}
	
	public enum Rating {
		ALL(""),
		SAFE("rating:safe%20-rating:questionable%20-rating:explicit%20"), 
		QUESTIONABLE("rating:questionable%20"),
		EXPLICIT("rating:explicit%20");

		private String name;

		Rating(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * Order of the posts
	 */
	public enum Sort {
		/** No order */
		RANDOM("sort:random%20"), 
		/** Less upvoted ones first */
		SCORE_ASC("sort:score:asc%20"),
		/** Most upvoted ones first */
		SCORE_DESC("sort:score:desc%20"),
		/** Oldest to newest */
		UPDATED_ASC("sort:updated:asc%20"),
		/** Newest to oldest */
		UPDATED_DESC("sort:updated:desc%20");

		private String name;

		Sort(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}