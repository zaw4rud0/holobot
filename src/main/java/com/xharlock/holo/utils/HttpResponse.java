package com.xharlock.holo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class HttpResponse {

	private Request.Builder builder;
	private OkHttpClient client;
	private JSONParser jsonParser;

	public HttpResponse() {
		this.builder = new Request.Builder();
		this.client = new OkHttpClient();
		this.jsonParser = new JSONParser();
	}

	public JSONObject getJSONObject(String url) throws IOException, ParseException {
		Request request = this.builder.url(new URL(url)).build();
		ResponseBody responseBody = this.client.newCall(request).execute().body();
		return (JSONObject) this.jsonParser.parse(responseBody.string());
	}

	public static JsonObject getJsonObject(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		return JsonParser.parseString(s).getAsJsonObject();
	}

	public static JsonArray getJsonArray(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		return (JsonArray) JsonParser.parseString(s);
	}

	public static String readLine(String url) throws IOException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(new URL(url).openConnection().getInputStream()));
		String line = in.readLine();
		in.close();
		return line;
	}
}
