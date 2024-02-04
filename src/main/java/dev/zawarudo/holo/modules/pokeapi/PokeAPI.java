package dev.zawarudo.holo.modules.pokeapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.zawarudo.holo.modules.pokeapi.model.Pokemon;
import dev.zawarudo.holo.modules.pokeapi.model.PokemonSpecies;
import dev.zawarudo.holo.modules.pokeapi.model.PokemonType;
import dev.zawarudo.holo.utils.HttpResponse;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class PokeAPI {

	/** The base URL for the PokeAPI. */
	public static final String BASE_URL = "https://pokeapi.co/api/v2/";
	/** The number of Pokémon that exist. */
	public static final int POKEMON_COUNT = 905;

	private PokeAPI() {
	}
	
	/**
	 * Returns a {@link PokemonSpecies} object from a given Pokédex id.
	 * 
	 * @param id The Pokédex id of the desired Pokémon species.
	 * @return A {@link PokemonSpecies} object
	 */
	public static PokemonSpecies getPokemonSpecies(int id) throws IOException, InvalidIdException {
		String url = BASE_URL + "pokemon-species/" + id + "/";
		JsonObject obj;
		try {
			obj = getJsonObject(url);
		} catch (NotFoundException ex) {
			throw new InvalidIdException("The given Pokédex id is invalid: " + id);
		}
		return new Gson().fromJson(obj, PokemonSpecies.class);
	}
	
	/**
	 * Returns a {@link PokemonSpecies} object from a given Pokémon species name.
	 * 
	 * @param name The name of the desired Pokémon species.
	 * @return A {@link PokemonSpecies} object
	 */
	public static PokemonSpecies getPokemonSpecies(String name) throws IOException, NotFoundException {
		String url = BASE_URL + "pokemon-species/" + escape(name) + "/";
		JsonObject obj;
		try {
			obj = getJsonObject(url);
		} catch (NotFoundException ex) {
			throw new NotFoundException("The given Pokémon species name is invalid: " + name);
		}
		return new Gson().fromJson(obj, PokemonSpecies.class);
	}

	/**
	 * Returns a {@link Pokemon} object from a given Pokédex id.
	 * 
	 * @param id The Pokédex id of the desired Pokémon.
	 * @return A {@link Pokemon} object
	 */
	public static Pokemon getPokemon(int id) throws IOException, InvalidIdException {
		String url = BASE_URL + "pokemon/" + id + "/";
		JsonObject obj;
		try {
			obj = getJsonObject(url);
		} catch (NotFoundException ex) {
			throw new InvalidIdException("The given Pokédex id is invalid: " + id);
		}
		return new Gson().fromJson(obj, Pokemon.class);
	}

	/**
	 * Returns a {@link Pokemon} object from a given Pokémon name.
	 * 
	 * @param name The name of the desired Pokémon.
	 * @return A {@link Pokemon} object
	 */
	public static Pokemon getPokemon(String name) throws IOException, NotFoundException {
		String url = BASE_URL + "pokemon/" + escape(name) + "/";
		JsonObject obj;
		try {
			obj = getJsonObject(url);
		} catch (NotFoundException ex) {
			throw new NotFoundException("The given Pokémon name is invalid: " + name);
		}
		return new Gson().fromJson(obj, Pokemon.class);
	}

	// TODO: JavaDoc
	public static PokemonType getType(int id) throws IOException, IllegalArgumentException {
		String url = String.format("%s/type/%d/", BASE_URL, id);
		JsonObject obj;
		try {
			obj = getJsonObject(url);
		} catch (NotFoundException ex) {
			throw new IllegalArgumentException("Invalid type id: " + id);
		}
		return new Gson().fromJson(obj, PokemonType.class);
	}

	// TODO: JavaDoc
	public static PokemonType getType(String name) throws IOException, IllegalArgumentException {
		String url = String.format("%s/type/%s/", BASE_URL, escape(name));
		JsonObject obj;
		try {
			obj = getJsonObject(url);
		} catch (NotFoundException ex) {
			throw new IllegalArgumentException("Invalid type name: " + name);
		}
		return new Gson().fromJson(obj, PokemonType.class);
	}

	/**
	 * Returns a random {@link PokemonSpecies} object from all existing Pokémon species.
	 * 
	 * @return A {@link PokemonSpecies} object
	 */
	public static PokemonSpecies getRandomPokemonSpecies() throws IOException, InvalidIdException {
		int id = getRandomNumber();
		return getPokemonSpecies(id);
	}

	/**
	 * Returns a random {@link Pokemon} object from all existing Pokémon species.
	 * 
	 * @return A {@link Pokemon} object
	 */
	public static Pokemon getRandomPokemon() throws IOException, InvalidIdException {
		int id = getRandomNumber();
		return getPokemon(id);
	}

	/**
	 * Fetches all the given ids and returns a list of {@link Pokemon}. Uses
	 * parallelization to be as quick as possible.
	 * 
	 * @param ids An array of Pokémon ids.
	 */
	public static List<Pokemon> getPokemon(int... ids) throws InterruptedException {
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

	private static JsonObject getJsonObject(String url) throws IOException, NotFoundException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		connection.setRequestMethod("GET");

		// OK
		if (connection.getResponseCode() == 200) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String raw = reader.lines().collect(Collectors.joining("\n"));
			return JsonParser.parseString(raw).getAsJsonObject();
		} 
		
		// Not found
		else if (connection.getResponseCode() == 404) {
			throw new NotFoundException();
		}
		
		// Something unexpected happened
		else {
			// TODO: Throw a more specific exception
			throw new IOException(connection.getResponseCode() + " " + connection.getResponseMessage());
		}
	}
	
	private static int getRandomNumber() throws IOException {
		String url = "https://www.random.org/integers/?num=1&min=1&max=" + POKEMON_COUNT + "&col=1&base=10&format=plain";
		return Integer.parseInt(HttpResponse.readLine(url));
	}
	
	/**
	 * Replaces weird characters.
	 */
	private static String escape(String name) {
		return name.toLowerCase(Locale.UK).replace(" ", "-")
				.replace(".", "")
				.replace(":", "-")
				.replace("'", "")
				.replace("♀", "-f")
				.replace("♂", "-m")
				.replace(":female_sign:", "-f")
				.replace(":male_sign:",	"-m");
	}
}

/**
 * Class to fetch a single Pokémon. Used to parallelize fetching multiple Pokémon.
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
		} catch (IOException | InvalidIdException ex) {
			ex.printStackTrace();
		}
	}
}