package com.xharlock.otakusenpai.apis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;

public class SauceNAOAPI {

	private static final String base_url = "https://saucenao.com/search.php?db=999&output_type=2&api_key=";

	public static JsonObject getJsonObject(String imageUrl) throws IOException {
		
		URL url = new URL(imageUrl);
		HttpURLConnection connection = new HttpURLConnection(url);
		
	
		// TODO Finish -> https://saucenao.com/user.php?page=search-api
		
	}
	
}
