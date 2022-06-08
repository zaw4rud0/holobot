package dev.zawarudo.holo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public final class Reader {

	private Reader() {
	}
	
	public static String readLine(String filePath) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return scanner != null ? scanner.nextLine() : null;
	}

	public static JsonObject readJsonObject(String filepath) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(filepath));
		return new Gson().fromJson(reader, JsonObject.class);
	}

	public static JsonArray readJsonArray(String filepath) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(filepath));
		return new Gson().fromJson(reader, JsonArray.class);
	}
}