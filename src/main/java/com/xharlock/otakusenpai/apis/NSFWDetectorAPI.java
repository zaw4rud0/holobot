package com.xharlock.otakusenpai.apis;

import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.xharlock.otakusenpai.core.Main;

public class NSFWDetectorAPI
{
    private static final String url = "https://api.deepai.org/api/nsfw-detector";
    
    public static double getNSFWScore(String imageUrl) throws IOException {
        String token = Main.otakuSenpai.getConfig().getKeyDeepAI();
        Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + url);
        String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines().collect(Collectors.joining("\n"));
        JsonObject object = JsonParser.parseString(result).getAsJsonObject();
        return object.getAsJsonObject("output").get("nsfw_score").getAsDouble();
    }
    
    public static JsonObject getJsonObject(String imageUrl) throws IOException {
    	String token = Main.otakuSenpai.getConfig().getKeyDeepAI();
        Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + url);
        String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines().collect(Collectors.joining("\n"));
        return JsonParser.parseString(result).getAsJsonObject();
    }
}