package com.xharlock.otakusenpai.apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TraceMoeAPI {
	
	private static final String url = "https://trace.moe/api/search";
	
	public static JsonObject getJsonObjectOld(String imageUrl) throws IOException {
		
		String command = "curl -F \"image=@" + imageUrl + "\" " + url;		
		Process pr = Runtime.getRuntime().exec(command);
		String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines().collect(Collectors.joining("\n"));
		
		System.out.println(result);
		
		return JsonParser.parseString(result).getAsJsonObject();
	}
	
	public static JsonObject getJsonObject(String imageUrl) throws IOException {		
		Process pr = Runtime.getRuntime().exec("curl -s https://trace.moe/api/search?url=" + imageUrl);
		String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines().collect(Collectors.joining("\n"));
		System.out.println(result);
		return JsonParser.parseString(result).getAsJsonObject();
	}
}
