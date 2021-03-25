package com.xharlock.otakusenpai.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

	public JSONObject getJSONObject(String urlQueryString) throws IOException, ParseException {
		Request request = this.builder.url(new URL(urlQueryString)).build();
		ResponseBody responseBody = this.client.newCall(request).execute().body();
		return (JSONObject) this.jsonParser.parse(responseBody.string());
	}

	public static JsonObject getJsonObject(String urlQueryString) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		connection.disconnect();
		return JsonParser.parseString(sb.toString()).getAsJsonObject();
	}

	public static JsonArray getJsonArray(String urlQueryString) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		connection.disconnect();
		return (JsonArray) JsonParser.parseString(sb.toString());
	}

	public static String readLine(String urlString) throws IOException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(new URL(urlString).openConnection().getInputStream()));
		String line = in.readLine();
		in.close();
		return line;
	}
}
