package dev.zawarudo.holo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;

public final class Writer {

	private Writer() {
	}

	public static void writeJsonObject(JsonObject obj, String filepath) throws IOException {
		FileWriter writer = new FileWriter(filepath);
		new Gson().toJson(obj, writer);
		writer.close();
	}

	public static void writeJsonArray(JsonArray array, String filepath) throws IOException {
		FileWriter writer = new FileWriter(filepath);
		new Gson().toJson(array, writer);
		writer.close();
	}
}