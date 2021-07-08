package com.xharlock.holo.games.pokemon;

import java.io.IOException;

import com.google.gson.JsonObject;

public class Pokemon extends PokemonSpecies implements Cloneable {

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

	/**
	 * Method to assign the gender of a Pokémon in relation to the gender ratio of their species
	 */
	private void assignGender() {
		// TODO
	}
	
	@Override
	public Pokemon clone() throws CloneNotSupportedException {
		Pokemon p = (Pokemon) super.clone();
		return p;
	}
}
