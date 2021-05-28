package com.xharlock.holo.utils;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {

	public static JSONObject readJSONObject(String filepath) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject object = (JSONObject) parser.parse(new FileReader(filepath));
		return object;
	}

	public static JSONArray readJSONArray(String filepath) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONArray array = (JSONArray) parser.parse(new FileReader(filepath));
		return array;
	}
}
