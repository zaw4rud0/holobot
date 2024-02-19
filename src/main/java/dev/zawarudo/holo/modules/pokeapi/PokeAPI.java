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
import java.util.concurrent.*;
import java.util.stream.Collectors;

public final class PokeAPI {

    /**
     * The base URL for the PokeAPI.
     */
    public static final String BASE_URL = "https://pokeapi.co/api/v2/";
    /**
     * The number of Pokémon that exist.
     */
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

    /**
     * Fetches all the information regarding the given type ID.
     */
    public static PokemonType getType(int typeId) throws IOException, IllegalArgumentException {
        String url = String.format("%s/type/%d/", BASE_URL, typeId);
        JsonObject obj;
        try {
            obj = getJsonObject(url);
        } catch (NotFoundException ex) {
            throw new IllegalArgumentException("Invalid type ID: " + typeId);
        }
        return new Gson().fromJson(obj, PokemonType.class);
    }

    /**
     * Fetches all the information regarding the given type name.
     */
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
    public static List<Pokemon> getPokemon(int... ids) throws InterruptedException, ExecutionException {
        int poolSize = Math.min(ids.length, Runtime.getRuntime().availableProcessors());

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<Future<Pokemon>> futures = new ArrayList<>();

        for (int id : ids) {
            Callable<Pokemon> task = () -> {
                try {
                    return PokeAPI.getPokemon(id);
                } catch (IOException | InvalidIdException ex) {
                    return null;
                }
            };
            futures.add(executor.submit(task));
        }

        List<Pokemon> pokemon = new ArrayList<>();
        for (Future<Pokemon> future : futures) {
            pokemon.add(future.get());
        }

        executor.shutdown();
        return pokemon;
    }

    private static JsonObject getJsonObject(String url) throws IOException, NotFoundException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", HttpResponse.USER_AGENT);
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
                .replace(":male_sign:", "-m");
    }
}