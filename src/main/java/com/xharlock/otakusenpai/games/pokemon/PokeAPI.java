package com.xharlock.otakusenpai.games.pokemon;

import java.io.IOException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

public class PokeAPI {

	private static final String baseUrl = "https://pokeapi.co/api/v2";
	private static final int PokemonCount = 898;

	public static JsonObject getPokemon(String name) throws IOException {
		String url = baseUrl + "/pokemon/" + name + "/";
		return getJsonObject(url);
	}

	public static JsonObject getPokemon(int id) throws IOException {
		String url = baseUrl + "/pokemon/" + id + "/";
		return getJsonObject(url);
	}

	public static JsonObject getPokemonSpecies(String name) throws IOException {
		String url = baseUrl + "/pokemon-species/" + name + "/";
		return getJsonObject(url);
	}

	public static JsonObject getPokemonSpecies(int id) throws IOException {
		String url = baseUrl + "/pokemon-species/" + id + "/";
		return getJsonObject(url);
	}

	public static int getPokemonCount() {
		return PokemonCount;
	}

	public static List<Pokemon> getRandomTeam() throws IOException, InterruptedException {
		List<Pokemon> team = new ArrayList<>();
		Random rand = new Random();
		List<Thread> threads = new ArrayList<>();
		List<PokemonFetcher> fetchers = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			int id = rand.nextInt(PokeAPI.getPokemonCount() + 1);
			PokemonFetcher fetcher = new PokemonFetcher(id);
			Thread t = new Thread(fetcher);
			threads.add(t);
			fetchers.add(fetcher);
			t.start();
		}		
		for (int i = 0; i < 6; i++) {
			threads.get(i).join();
			team.add(fetchers.get(i).pokemon);
		}		
		return team;
	}

	private static JsonObject getJsonObject(String urlQueryString) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		connection.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		connection.disconnect();
		return JsonParser.parseString(s).getAsJsonObject();
	}
}

class PokemonFetcher implements Runnable {
	int id;
	Pokemon pokemon;

	public PokemonFetcher(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		try {
			this.pokemon = new Pokemon(PokeAPI.getPokemonSpecies(id));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}