package com.xharlock.holo.games.pokemon;

import java.io.IOException;

import com.google.gson.JsonObject;

public class Pokemon extends PokemonSpecies {

	public String nickname;
	public String gender;
	public int level;
	public long experience;
	public String ability;
	public String form;
	
	public Pokemon(JsonObject species) throws IOException {
		super(species);
		assignGender();
	}

	private void assignGender() {
		
	}
	
	@Override
	public Pokemon clone() throws CloneNotSupportedException {
		Pokemon p = (Pokemon) super.clone();
		return p;
	}
}
