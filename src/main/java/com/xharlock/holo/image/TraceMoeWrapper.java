package com.xharlock.holo.image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TraceMoeWrapper {
	
	private static final String url = "https://trace.moe/api/search";
	
	public static JsonObject getJsonObject(String imageUrl) throws IOException {		
		Process pr = Runtime.getRuntime().exec("curl -s " + url + "?url=" + imageUrl);
		String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines().collect(Collectors.joining("\n"));
		System.out.println(result);
		return JsonParser.parseString(result).getAsJsonObject();
	}
}
