package com.xharlock.holo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
		String s = scanner.nextLine();
		scanner.close();
		return s;
	}
	
	public static JSONObject readJSONObject(String filepath) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(new FileReader(filepath));
	}

	public static JSONArray readJSONArray(String filepath) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		return (JSONArray) parser.parse(new FileReader(filepath));
	}
}