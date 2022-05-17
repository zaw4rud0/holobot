package com.xharlock.holo.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public final class HttpResponse {

	private HttpResponse() {
	}

	public static JsonObject getJsonObject(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		connection.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));

		System.out.println(s);


		return JsonParser.parseString(s).getAsJsonObject();
	}

	public static JsonArray getJsonArray(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		return (JsonArray) JsonParser.parseString(s);
	}

	public static String readLine(String url) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
		return reader.readLine();
	}
}