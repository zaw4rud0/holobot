package com.xharlock.otakusenpai.anime;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class Manga {

	public String title;
	public String title_en;
	public String title_jp;
	public int mal_id;
	public String url;
	public String image_url;
	public String synopsis;
	public String type;
	public String status;
	public int popularity;
	public double score;
	public int rank;
	public int volumes;
	public int chapters;
	public String prequel;
	public String sequel;
	public ArrayList<String> authors;
	public ArrayList<String> genres;
	public String author_ap;
	public String chapters_ap;
	public String year_ap;
	public String[] genres_ap;

	public Manga(JsonObject object) {
		if (object == null) {
			return;
		}
		this.authors = new ArrayList<String>();
		this.genres = new ArrayList<String>();
		this.title = object.get("title").getAsString();
		if (!object.get("title_english").isJsonNull()) {
			this.title_en = object.get("title_english").getAsString();
		}
		this.title_jp = object.get("title_japanese").getAsString();
		this.mal_id = object.get("mal_id").getAsInt();
		this.url = object.get("url").getAsString();
		this.image_url = object.get("image_url").getAsString();
		if (!object.get("synopsis").isJsonNull()) {
			this.synopsis = object.get("synopsis").getAsString().replace(" [Written by MAL Rewrite]", "");
		}
		this.type = object.get("type").getAsString();
		this.status = object.get("status").getAsString();
		this.popularity = object.get("popularity").getAsInt();
		if (!object.get("score").isJsonNull()) {
			this.score = object.get("score").getAsDouble();
		}
		if (!object.get("chapters").isJsonNull()) {
			this.chapters = object.get("chapters").getAsInt();
		}
		if (!object.get("volumes").isJsonNull()) {
			this.volumes = object.get("volumes").getAsInt();
		}
		for (int i = 0; i < object.get("genres").getAsJsonArray().size(); ++i) {
			this.genres.add(object.get("genres").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString());
		}
	}

	// TODO Constructor for an Anime-Planet Manga object
	public Manga(String title2, String chapters2, String image, String year, String description, String[] tags, String url2) {
	
	}
}
