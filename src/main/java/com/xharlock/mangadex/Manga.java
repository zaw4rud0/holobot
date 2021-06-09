package com.xharlock.mangadex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Manga {

	public String id;
	public String title;
	public String description;
	public String original_language; // TODO
	public String demographic; // TODO
	public String status;
	public String year; // TODO
	public String content_rating;
	public HashMap<String, String> tags;
	
	public List<String> authors; // TODO
	public List<String> artists; // TODO
	public String cover_art;
	
	public Manga(JsonObject object) {
		JsonObject data = object.getAsJsonObject().getAsJsonObject("data");
		JsonObject attributes = data.getAsJsonObject("attributes");
		
		this.id = data.get("id").getAsString();
		this.title = attributes.getAsJsonObject("title").get("en").getAsString().replace("'", "''");
		this.description = attributes.getAsJsonObject("description").get("en").getAsString().replace("'", "''");
		this.original_language = attributes.get("originalLanguage").getAsString();
		this.demographic = attributes.get("publicationDemographic").getAsString();
		this.status = attributes.get("status").getAsString();
		this.year = attributes.get("year").getAsString();
		this.content_rating = attributes.get("contentRating").getAsString();
		
		// Get tags
		this.tags = new HashMap<>();
		JsonArray tags_raw = attributes.getAsJsonArray("tags");
		
		for (int m = 0; m < tags_raw.size(); m++) {
			JsonObject obj = tags_raw.get(m).getAsJsonObject();
			String id = obj.get("id").getAsString();
			String name = obj.getAsJsonObject("attributes").getAsJsonObject("name").get("en").getAsString().replace("'", "''");
			tags.put(name, id);
		}
		
		// Get stuff from relationships
		JsonArray relationships = object.getAsJsonObject().getAsJsonArray("relationships");
		
		authors = new ArrayList<>();
		artists = new ArrayList<>();
		
		for (int k = 0; k < relationships.size(); k++) {
			JsonObject obj = relationships.get(k).getAsJsonObject();
			if (obj.get("type").getAsString().equals("author")) {
				if (!obj.get("id").isJsonNull())
				authors.add(obj.get("id").getAsString());
			}
			if (obj.get("type").getAsString().equals("artist")) {
				if (!obj.get("id").isJsonNull())
				artists.add(obj.get("id").getAsString());
			}
			if (obj.get("type").getAsString().equals("cover_art")) {
				if (!obj.get("id").isJsonNull())
				cover_art = obj.get("id").getAsString();
			}
		}
	}

}
