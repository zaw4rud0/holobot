package com.xharlock.otakusenpai.apis;

import java.io.IOException;

import com.google.gson.JsonObject;

public class Testing {

	public static void main(String[] args) {
		
		String url = "https://mangathrill.com/wp-content/uploads/2020/04/luffyyass.jpg";
		
		JsonObject object = null;
		
		try {
			object = TraceMoeAPI.getJsonObject(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(object);
		
	}
}
