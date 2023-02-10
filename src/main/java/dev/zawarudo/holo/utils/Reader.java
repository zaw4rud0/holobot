package dev.zawarudo.holo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		BufferedReader reader = Files.newBufferedReader(Paths.get(filepath));
		return new Gson().fromJson(reader, JsonObject.class);
	}

	public static JsonArray readJsonArray(String filepath) throws IOException {
		BufferedReader reader = Files.newBufferedReader(Paths.get(filepath));
		return new Gson().fromJson(reader, JsonArray.class);
	}
}