package com.xharlock.holo.games.pokemon;

import com.google.gson.annotations.SerializedName;

/**
 * A {@link Nameable} object without url and that is dependent on the language
 */
public class LangNameable {
	@SerializedName(value = "name", alternate = {"genus", "description"})
	public String name;
	@SerializedName("language")
	public Nameable language;
}
