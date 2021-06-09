package com.xharlock.mangadex;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Chapter {

	public String id;
	public String hash;
	public String title;
	public String volume;
	public String chapter;

	public String translatedLanguage;
	public String publishAt;
	public String createdAt;
	public String updatedAt;

	public List<String> pages_data;
	public List<String> pages_dataSaver;

	public String scanlation_group;
	public String manga_id;

	public Chapter(JsonObject object) {
		JsonObject data = object.getAsJsonObject().getAsJsonObject("data");
		JsonObject attributes = data.getAsJsonObject("attributes");

		this.id = data.get("id").getAsString();
		this.hash = attributes.get("hash").getAsString();
		this.title = attributes.get("title").getAsString().replace("'", "''");

		if (!attributes.get("volume").isJsonNull())
			this.volume = attributes.get("volume").getAsString();
		if (!attributes.get("chapter").isJsonNull())
			this.chapter = attributes.get("chapter").getAsString();
		if (!attributes.get("translatedLanguage").isJsonNull())
			this.translatedLanguage = attributes.get("translatedLanguage").getAsString();
		if (!attributes.get("publishAt").isJsonNull())
			this.publishAt = attributes.get("publishAt").getAsString();
		if (!attributes.get("createdAt").isJsonNull())
			this.createdAt = attributes.get("createdAt").getAsString();
		if (!attributes.get("updatedAt").isJsonNull())
			this.updatedAt = attributes.get("updatedAt").getAsString();

		// Get pages with normal resolution
		this.pages_data = new ArrayList<>();
		JsonArray pages_data = attributes.getAsJsonArray("data");
		for (int i = 0; i < pages_data.size(); i++) {
			this.pages_data.add(pages_data.get(i).getAsString());
		}

		// Get pages with lower resolution
		pages_dataSaver = new ArrayList<>();
		JsonArray pages_dataSaver = attributes.getAsJsonArray("dataSaver");
		for (int i = 0; i < pages_data.size(); i++) {
			this.pages_dataSaver.add(pages_dataSaver.get(i).getAsString());
		}

		JsonArray relationships = object.getAsJsonArray("relationships");
		for (int k = 0; k < relationships.size(); k++) {
			JsonObject obj = relationships.get(k).getAsJsonObject();
			if (obj.get("type").getAsString().equals("scanlation_group")) {
				if (!obj.get("id").isJsonNull())
					this.scanlation_group = obj.get("id").getAsString();
			}
			if (obj.get("type").getAsString().equals("manga")) {
				if (!obj.get("id").isJsonNull())
					this.manga_id = obj.get("id").getAsString();
			}
		}
	}
}
