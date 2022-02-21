package com.xharlock.holo.games.pokemon;

import java.io.IOException;

import com.xharlock.pokeapi4java.PokeAPI;
import com.xharlock.pokeapi4java.model.Pokemon;

/**
 * Class that manages where and when a {@link Pokemon} spawns.
 */
public class PokemonSpawnManager {

	public void spawnPokemon() {
		Pokemon pokemon = null;
		try {
			pokemon = PokeAPI.getRandomPokemon();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(pokemon.name);
	}
}
