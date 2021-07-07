package com.xharlock.holo.experimental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class YoutubeAPI {

	private static final String base_url = "https://www.googleapis.com/youtube/v3/";
	private static final String api_key = "AIzaSyDstZh_g5qTkpsa4QcGgkQOwtMCbY94sP8";

	public static void main(String[] args) throws IOException {
		String channel_id = "UC-3SbfTPJsL8fJAPKiVqBLg";
		JsonArray array = getVideos(channel_id, 50);
		System.out.println("https://www.youtube.com/watch?v="
				+ array.get(49).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString());
	}

	/**
	 * Method to get a given number of videos of a given channel.<br>
	 * Ordered by date.<br>
	 * 
	 * @param channel_id = Youtube id of the channel
	 * @param max_results = 0 to 50 (inclusive)
	 */
	public static JsonArray getVideos(String channel_id, int max_results) throws IOException {
		String url = base_url + "search?part=snippet&channelId=" + channel_id + "&maxResults=" + max_results
				+ "&order=date&type=video&key=" + api_key;
		JsonObject obj = getJsonObject(url);
		return obj.getAsJsonArray("items");
	}

	private static JsonObject getJsonObject(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		conn.disconnect();
		return JsonParser.parseString(s).getAsJsonObject();
	}
}
