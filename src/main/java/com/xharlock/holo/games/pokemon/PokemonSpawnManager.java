package com.xharlock.holo.games.pokemon;

import java.io.IOException;

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
