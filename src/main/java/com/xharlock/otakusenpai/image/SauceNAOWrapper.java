package com.xharlock.otakusenpai.image;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;

public class SauceNAOWrapper {

	private static final String base_url = "https://saucenao.com/search.php?db=999&output_type=2&api_key=";

	public static JsonObject getJsonObject(String imageUrl) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
		
	
		// TODO Finish -> https://saucenao.com/user.php?page=search-api
		
	}
	
}
