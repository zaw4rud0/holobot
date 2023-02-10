package dev.zawarudo.holo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Writer {

	private Writer() {
	}

	public static void writeToFile(JsonObject obj, String filepath) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(filepath));
		new Gson().toJson(obj, writer);
		writer.close();
	}

	public static void writeToFile(JsonArray array, String filepath) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(filepath));
		new Gson().toJson(array, writer);
		writer.close();
	}
}