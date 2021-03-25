package com.xharlock.otakusenpai.apis;

import java.io.IOException;
import com.google.gson.JsonParser;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.xharlock.otakusenpai.core.Main;

public class Waifu2xWrapper {
	private static final String url = "https://api.deepai.org/api/waifu2x";

	/**
	 * Method to send an image to waifu2x and returns the url of the upscaled image
	 * 
	 * @param imageUrl = Url to the image to be upscaled
	 * @return Url to the upscaled image
	 * @throws IOException
	 */
	public static String upscaleImage(String imageUrl) throws IOException {
		String token = Main.otakuSenpai.getConfig().getKeyDeepAI();
		Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String result = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		return JsonParser.parseString(result).getAsJsonObject().get("output_url").getAsString();
	}
}