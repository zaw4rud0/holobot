package com.xharlock.otakusenpai.games.pokemon;

import java.io.IOException;

import com.google.gson.JsonObject;

public class Pokemon extends PokemonSpecies {

	public Pokemon(JsonObject species) throws IOException {
		super(species);
	}

}
