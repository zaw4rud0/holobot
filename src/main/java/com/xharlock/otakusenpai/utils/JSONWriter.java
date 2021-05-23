package com.xharlock.otakusenpai.utils;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWriter {

	public static void writeJSONObject(JSONObject obj, String filepath) throws IOException {
		FileWriter writer = new FileWriter(filepath);
		writer.write(obj.toJSONString());
		writer.flush();
		writer.close();
	}

	public static void writeJSONArray(JSONArray array, String filepath) throws IOException {
		FileWriter writer = new FileWriter(filepath);
		writer.write(array.toJSONString());
		writer.flush();
		writer.close();
	}
}
