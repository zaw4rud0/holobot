package com.xharlock.otakusenpai.anime;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xharlock.otakusenpai.utils.Formatter;

public class MangaDexAPI {

	// TODO Rewrite and generalize methods
	
	private static String baseUrl = "https://api.mangadex.org/";

	// Get chapters of a manga, e.g. Solo Leveling:
	// https://api.mangadex.org/manga/32d76d19-8a05-4db0-9fc2-e0b0648fe9d0/feed?limit=500
	private static String chapterFeed = baseUrl	+ "manga/{id}/feed?limit=500&order[chapter]=asc&translatedLanguage[]=en";

	public static JsonArray search(String terms) throws IOException {
		String url = baseUrl + "manga?limit=10&title=" + Formatter.escapeCharacters(terms);
		return getJsonObject(url).getAsJsonArray("results");
	}

	public static String getChapterId(String manga_id, String chapter) throws IOException {
		JsonArray chapters = getChapters(manga_id);
		for (int i = 0; i < chapters.size(); i++)
			if (chapters.get(i).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("attributes").get("chapter")
					.getAsString().equals(chapter))
				return chapters.get(i).getAsJsonObject().getAsJsonObject("data").get("id").getAsString();
		return null;
	}

	public static JsonArray getChapters(String manga_id) throws IOException {
		String url = chapterFeed.replace("{id}", manga_id);
		return getJsonObject(url).get("results").getAsJsonArray();
	}

	public static List<String> getPages(String chapter_id, boolean dataSaver) throws IOException, InterruptedException {
		String url = baseUrl + "chapter/" + chapter_id;
		JsonObject object = getJsonObject(url);
		String chapter_hash = object.getAsJsonObject("data").getAsJsonObject("attributes").get("hash").getAsString();

		JsonArray pages_raw = null;

		if (dataSaver)
			pages_raw = object.getAsJsonObject("data").getAsJsonObject("attributes").getAsJsonArray("dataSaver");
		else
			pages_raw = object.getAsJsonObject("data").getAsJsonObject("attributes").getAsJsonArray("data");

		List<String> pages = new ArrayList<>();

		for (int i = 0; i < pages_raw.size(); i++) {
			Thread.sleep(100);
			String page_url = getPageUrl(chapter_id, chapter_hash, pages_raw.get(i).getAsString(), dataSaver);			
			System.out.println(page_url);			
			pages.add(page_url);
		}
		return pages;
	}
	
	public static List<BufferedImage> getPageImages(String chapter_id, boolean dataSaver) throws IOException, InterruptedException {
		String url = baseUrl + "chapter/" + chapter_id;
		JsonObject object = getJsonObject(url);
		String chapter_hash = object.getAsJsonObject("data").getAsJsonObject("attributes").get("hash").getAsString();

		JsonArray pages_raw = null;

		if (dataSaver)
			pages_raw = object.getAsJsonObject("data").getAsJsonObject("attributes").getAsJsonArray("dataSaver");
		else
			pages_raw = object.getAsJsonObject("data").getAsJsonObject("attributes").getAsJsonArray("data");

		List<BufferedImage> pages = new ArrayList<>();

		for (int i = 0; i < pages_raw.size(); i++) {			
			String page_url = getPageUrl(chapter_id, chapter_hash, pages_raw.get(i).getAsString(), dataSaver);
			pages.add(ImageIO.read(new URL(page_url)));
		}
		return pages;
	}

	public static String getPageUrl(String chapter_id, String chapter_hash, String file_name, boolean dataSaver)
			throws IOException, InterruptedException {
		Thread.sleep(100);		
		JsonObject obj = getJsonObject(baseUrl + "at-home/server/" + chapter_id);
		String pageUrl = "";
		if (dataSaver)
			pageUrl = obj.get("baseUrl").getAsString() + "/data-saver/" + chapter_hash + "/" + file_name;
		else
			pageUrl = obj.get("baseUrl").getAsString() + "/data/" + chapter_hash + "/" + file_name;
		return pageUrl;
	}

	private static JsonObject getJsonObject(String urlQueryString) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		return JsonParser.parseString(s).getAsJsonObject();
	}
}
