package com.xharlock.otakusenpai.anime;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class Anime {

	public String title;
	public String title_en;
	public String title_jp;
	public int mal_id;
	public String url;
	public String image_url;
	public String synopsis;
	public String type;
	public String season;
	public String status;
	public int popularity;
	public double score;
	public int rank;
	public String source;
	public String duration;
	public int episodes;
	public String prequel;
	public String sequel;
	public ArrayList<String> studios;
	public ArrayList<String> genres;

	public Anime(JsonObject object) {
		if (object == null) {
			return;
		}
		this.studios = new ArrayList<String>();
		this.genres = new ArrayList<String>();
		this.title = object.get("title").getAsString();
		if (!object.get("title_english").isJsonNull()) {
			this.title_en = object.get("title_english").getAsString();
		}
		this.title_jp = object.get("title_japanese").getAsString();
		this.mal_id = object.get("mal_id").getAsInt();
		this.url = object.get("url").getAsString();
		this.image_url = object.get("image_url").getAsString();
		this.synopsis = object.get("synopsis").getAsString().replace(" [Written by MAL Rewrite]", "");
		this.type = object.get("type").getAsString();
		if (!object.get("premiered").isJsonNull()) {
			this.season = object.get("premiered").getAsString();
		}
		this.status = object.get("status").getAsString();
		this.popularity = object.get("popularity").getAsInt();
		if (!object.get("score").isJsonNull()) {
			this.score = object.get("score").getAsDouble();
		}
		if (!object.get("rank").isJsonNull()) {
			this.rank = object.get("rank").getAsInt();
		}
		this.source = object.get("source").getAsString();
		this.duration = object.get("duration").getAsString();
		if (!object.get("episodes").isJsonNull()) {
			this.episodes = object.get("episodes").getAsInt();
		}
		for (int i = 0; i < object.get("genres").getAsJsonArray().size(); ++i) {
			this.genres.add(object.get("genres").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString());
		}
	}

	// TODO Constructor for an Anime-Planet Anime object
	public Anime(String title2, String alternativeTitle, String image, String episodes2, String year, AnimeSeason season2, String description, String[] tags, String studio, String url2) {
		
	}
}
