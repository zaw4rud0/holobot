package com.xharlock.holo.games.pokemon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PokeAPI {

	private static final String baseUrl = "https://pokeapi.co/api/v2/";
	/** The number of Pokémon */
	public static final int pokemonCount = 898;

	private PokeAPI() {
	}

	public static PokemonSpecies getPokemonSpecies(int id) throws IOException {
		String url = baseUrl + "pokemon-species/" + id + "/";
		JsonObject obj = getJsonObject(url);
		return new Gson().fromJson(obj, PokemonSpecies.class);
	}

	public static PokemonSpecies getPokemonSpecies(String name) throws IOException {
		String url = baseUrl + "pokemon-species/" + escape(name) + "/";
		JsonObject obj = getJsonObject(url);
		return new Gson().fromJson(obj, PokemonSpecies.class);
	}

	public static Pokemon getPokemon(int id) throws IOException {
		String url = baseUrl + "pokemon/" + id + "/";
		JsonObject obj = getJsonObject(url);
		return new Gson().fromJson(obj, Pokemon.class);
	}

	public static Pokemon getPokemon(String name) throws IOException {
		String url = baseUrl + "pokemon/" + escape(name) + "/";
		JsonObject obj = getJsonObject(url);
		return new Gson().fromJson(obj, Pokemon.class);
	}

	public static PokemonSpecies getRandomPokemonSpecies() throws IOException {
		int id = new Random().nextInt(pokemonCount) + 1;
		return getPokemonSpecies(id);
	}

	public static Pokemon getRandomPokemon() throws IOException {
		int id = new Random().nextInt(pokemonCount) + 1;
		return getPokemon(id);
	}

	/**
	 * Fetches all the given ids and returns a list of {@link Pokemon}. Uses
	 * parallelization to be as quick as possible.
	 */
	public static List<Pokemon> getPokemons(int... ids) throws InterruptedException {
		List<Pokemon> pokes = new ArrayList<>();
		List<Thread> threads = new ArrayList<>();
		List<PokemonFetcher> fetchers = new ArrayList<>();

		// Start all threads
		for (int id : ids) {
			PokemonFetcher fetcher = new PokemonFetcher(id);
			Thread t = new Thread(fetcher);
			threads.add(t);
			fetchers.add(fetcher);
			t.start();
		}

		// Wait for all threads to finish
		for (int i = 0; i < ids.length; i++) {
			threads.get(i).join();
			pokes.add(fetchers.get(i).pokemon);
		}
		return pokes;
	}

	private static JsonObject getJsonObject(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		connection.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String raw = reader.lines().collect(Collectors.joining("\n"));
		return JsonParser.parseString(raw).getAsJsonObject();
	}
	
	/**
	 * Replaces weird characters
	 */
	private static String escape(String name) {
		return name.replace(" ", "-")
				.replace(".", "")
				.replace(":", "-")
				.replace("'", "")
				.replace("\u2640", "-f")
				.replace("\u2642", "-m")
				.replace(":female_sign:", "-f")
				.replace(":male_sign:",	"-m");
	}
}

/**
 * Class to fetch a single Pokémon. Used to parallize fetching multiple
 * Pokémons.
 */
class PokemonFetcher implements Runnable {
	int id;
	Pokemon pokemon;

	public PokemonFetcher(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		try {
			pokemon = PokeAPI.getPokemon(id);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
