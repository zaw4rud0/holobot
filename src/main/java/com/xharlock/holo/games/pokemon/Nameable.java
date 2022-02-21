package com.xharlock.holo.games.pokemon;

import com.google.gson.annotations.SerializedName;

/**
 * An object with a name and an url
 */
public class Nameable {
	@SerializedName("name")
	public String name;
	@SerializedName("url")
	public String url;
}
