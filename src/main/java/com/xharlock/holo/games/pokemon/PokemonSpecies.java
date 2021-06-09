package com.xharlock.holo.games.pokemon;

import java.io.IOException;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xharlock.holo.utils.Formatter;
import com.xharlock.holo.utils.HttpResponse;

public class PokemonSpecies {

	public String name;
	public String pokedexId;
	public String genus;
	public String generation;
	public String pokedexEntry;
	public PokemonType type1;
	public PokemonType type2;
	/** The chance of this Pokémon being female with -1 for genderless. */
	public double genderRate;
	public String height;
	public String weight;
	public String abilities;
	public String sprite_front;
	public String sprite_back;
	public String artwork;
	public String animated;
	public String evolutionChain;
	public boolean isBaby = false;
	public boolean isMythical = false;
	public boolean isLegendary = false;
	public boolean isUltraBeast = false;
	public boolean formsSwitchable = false;
	public String forms;

	public PokemonSpecies(JsonObject species) throws IOException {
		if (species == null) {
			return;
		}
		
		JsonObject pokemon = null;

		for (int i = 0; i < species.getAsJsonArray("varieties").size(); i++) {
			if (species.getAsJsonArray("varieties").get(i).getAsJsonObject().get("is_default").getAsBoolean()) {
				pokemon = HttpResponse.getJsonObject(species.getAsJsonArray("varieties").get(i).getAsJsonObject()
						.get("pokemon").getAsJsonObject().get("url").getAsString());
			}
		}
		
		JsonObject evolution = HttpResponse
				.getJsonObject(species.getAsJsonObject("evolution_chain").get("url").getAsString());
		this.pokedexId = this.getPokedexNumber(species);
		this.name = this.getName(species);
		this.genus = this.getGenus(species);
		this.pokedexEntry = this.getPokedexEntry(species);
		this.abilities = this.getAbility(pokemon);
		this.generation = species.get("generation").getAsJsonObject().get("name").getAsString().toUpperCase()
				.replace("GENERATION-", "Gen ");
		this.genderRate = this.getGenderRate(species);
		this.height = pokemon.get("height").getAsDouble() / 10.0 + " m";
		this.weight = pokemon.get("weight").getAsDouble() / 10.0 + " kg";
		this.sprite_front = this.getSprite(pokemon, "front_default");
		this.sprite_back = this.getSprite(pokemon, "back_default");
		this.artwork = this.getArtwork(pokemon);
		this.animated = null;
		setPokemonTypes(pokemon);
		this.evolutionChain = this.getEvolutionChain(evolution);
		this.isBaby = species.get("is_baby").getAsBoolean();
		this.isLegendary = species.get("is_legendary").getAsBoolean();
		this.isMythical = species.get("is_mythical").getAsBoolean();
		this.isUltraBeast = this.isUltraBeast();
	}

	protected String getPokedexNumber(JsonObject species) {
		for (int i = 0; i < species.get("pokedex_numbers").getAsJsonArray().size(); i++) {
			if (species.get("pokedex_numbers").getAsJsonArray().get(i).getAsJsonObject().get("pokedex")
					.getAsJsonObject().get("name").getAsString().equals("national")) {
				return String.format("%03d", species.get("pokedex_numbers").getAsJsonArray().get(i).getAsJsonObject()
						.get("entry_number").getAsInt());
			}
		}
		return null;
	}

	protected String getName(JsonObject species) {
		String s = "";
		for (int i = 0; i < species.getAsJsonArray("names").size(); i++) {
			if (species.getAsJsonArray("names").get(i).getAsJsonObject().getAsJsonObject("language").get("name")
					.getAsString().equals("en")) {
				s = species.getAsJsonArray("names").get(i).getAsJsonObject().get("name").getAsString()
						.replaceAll("\u2640", "\\\u2640").replaceAll("\u2642", "\\\u2642");
			}
		}
		return s;
	}

	protected String getPokedexEntry(JsonObject species) {
		Random rand = new Random();
		List<String> entries = new ArrayList<String>();
		JsonArray arr = species.get("flavor_text_entries").getAsJsonArray();
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getAsJsonObject().getAsJsonObject("language").get("name").getAsString().equals("en")) {
				String s = arr.get(i).getAsJsonObject().get("flavor_text").getAsString().replaceAll("\\n", " ")
						.replaceAll(System.getProperty("line.separator"), " ").replace("\f", " ")
						.replaceAll("POK\u00e9MON", "Pok\u00e9mon").replaceAll(this.name.toUpperCase(), this.name);
				entries.add(s);
			}
		}
		return entries.get(rand.nextInt(entries.size()));
	}

	protected String getSprite(JsonObject pokemon, String spriteName) {
		if (pokemon.getAsJsonObject("sprites").get(spriteName).isJsonNull())
			return null;
		else
			return pokemon.getAsJsonObject("sprites").get(spriteName).getAsString();
	}

	protected String getArtwork(JsonObject pokemon) {
		return pokemon.getAsJsonObject("sprites").getAsJsonObject("other").getAsJsonObject("official-artwork")
				.get("front_default").getAsString();
	}

	protected String getGenus(JsonObject species) {
		String s = "";
		for (int i = 0; i < species.get("genera").getAsJsonArray().size(); i++) {
			if (species.get("genera").getAsJsonArray().get(i).getAsJsonObject().get("language").getAsJsonObject()
					.get("name").getAsString().equals("en")) {
				s = species.get("genera").getAsJsonArray().get(i).getAsJsonObject().get("genus").getAsString();
			}
		}
		return s;
	}
	
	protected double getGenderRate(JsonObject species) {		
		int ratio = species.get("gender_rate").getAsInt();
		return ratio == -1 ? -1.0  : ratio / 8.0;
	}

	protected void setPokemonTypes(JsonObject pokemon) {
		if (pokemon.get("types").getAsJsonArray().size() == 1) {
			this.type1 = setPokemonTypesHelper(pokemon.getAsJsonArray("types").get(0).getAsJsonObject()
					.getAsJsonObject("type").get("name").getAsString());
		} else {
			this.type1 = setPokemonTypesHelper(pokemon.getAsJsonArray("types").get(0).getAsJsonObject()
					.getAsJsonObject("type").get("name").getAsString());
			this.type2 = setPokemonTypesHelper(pokemon.getAsJsonArray("types").get(1).getAsJsonObject()
					.getAsJsonObject("type").get("name").getAsString());
		}
	}

	protected PokemonType setPokemonTypesHelper(String type) {
		switch (type.toLowerCase()) {
		case ("normal"):
			return PokemonType.NORMAL;
		case ("fire"):
			return PokemonType.FIRE;
		case ("fighting"):
			return PokemonType.FIGHTING;
		case ("flying"):
			return PokemonType.FLYING;
		case ("water"):
			return PokemonType.WATER;
		case ("grass"):
			return PokemonType.GRASS;
		case ("electric"):
			return PokemonType.ELECTRIC;
		case ("poison"):
			return PokemonType.POISON;
		case ("dark"):
			return PokemonType.DARK;
		case ("fairy"):
			return PokemonType.FAIRY;
		case ("psychic"):
			return PokemonType.PSYCHIC;
		case ("steel"):
			return PokemonType.STEEL;
		case ("rock"):
			return PokemonType.ROCK;
		case ("ground"):
			return PokemonType.GROUND;
		case ("bug"):
			return PokemonType.BUG;
		case ("dragon"):
			return PokemonType.DRAGON;
		case ("ghost"):
			return PokemonType.GHOST;
		case ("ice"):
			return PokemonType.ICE;
		default:
			return null;
		}
	}

	protected String getCurrentForm(JsonObject pokemon) {
		String s = "";
		s = pokemon.get("name").getAsString();
		return Formatter.firstLetterUp(s);
	}

	protected String getForms(JsonObject species, String name) {
		String forms = "";
		for (int i = 0; i < species.get("varieties").getAsJsonArray().size(); i++) {
			if (!species.get("varieties").getAsJsonArray().get(i).getAsJsonObject().get("pokemon").getAsJsonObject()
					.get("name").getAsString().equals(name)) {
				forms = String.valueOf(forms)
						+ Formatter.firstLetterUp(String.valueOf(species.get("varieties").getAsJsonArray().get(i)
								.getAsJsonObject().get("pokemon").getAsJsonObject().get("name").getAsString()) + "\n");
			}
		}
		return forms;
	}

	protected String getAbility(JsonObject pokemon) {
		String ability = "";
		for (int i = 0; i < pokemon.get("abilities").getAsJsonArray().size(); i++) {
			ability = String.valueOf(ability)
					+ Formatter.firstLetterUp(String.valueOf(pokemon.get("abilities").getAsJsonArray().get(i)
							.getAsJsonObject().get("ability").getAsJsonObject().get("name").getAsString()) + "\n");
		}
		return ability;
	}

	protected String getEvolutionChain(JsonObject pokemonEvolution) {
		String chain = "ERROR";
		if (pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to").size() == 0) {
			chain = null;
		} else if (pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to").get(0).getAsJsonObject()
				.getAsJsonArray("evolves_to").size() == 0) {
			String stage1 = Formatter.firstLetterUp(
					pokemonEvolution.getAsJsonObject("chain").getAsJsonObject("species").get("name").getAsString());
			chain = String.valueOf(stage1) + " \u2192 "
					+ Formatter.firstLetterUp(pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to")
							.get(0).getAsJsonObject().getAsJsonObject("species").get("name").getAsString());
			for (int i = 1; i < pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to").size(); i++) {
				chain = String.valueOf(chain) + "\n" + stage1 + " \u2192 "
						+ Formatter.firstLetterUp(pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to")
								.get(i).getAsJsonObject().getAsJsonObject("species").get("name").getAsString());
			}
		} else {
			String stage1 = Formatter.firstLetterUp(
					pokemonEvolution.getAsJsonObject("chain").getAsJsonObject("species").get("name").getAsString());
			if (pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to").size() > 1) {
				chain = String.valueOf(stage1) + " \u2192 "
						+ Formatter.firstLetterUp(pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to")
								.get(0).getAsJsonObject().getAsJsonObject("species").get("name").getAsString())
						+ " \u2192 "
						+ Formatter.firstLetterUp(pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to")
								.get(0).getAsJsonObject().getAsJsonArray("evolves_to").get(0).getAsJsonObject()
								.getAsJsonObject("species").get("name").getAsString());
				for (int i = 1; i < pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to")
						.size(); i++) {
					chain = String.valueOf(chain) + "\n" + stage1 + " \u2192 "
							+ Formatter.firstLetterUp(
									pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to").get(i)
											.getAsJsonObject().getAsJsonObject("species").get("name").getAsString())
							+ " \u2192 "
							+ Formatter.firstLetterUp(pokemonEvolution.getAsJsonObject("chain")
									.getAsJsonArray("evolves_to").get(i).getAsJsonObject().getAsJsonArray("evolves_to")
									.get(0).getAsJsonObject().getAsJsonObject("species").get("name").getAsString());
				}
			} else {
				String stage2 = Formatter
						.firstLetterUp(pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to").get(0)
								.getAsJsonObject().getAsJsonObject("species").get("name").getAsString());
				chain = String.valueOf(stage1) + " \u2192 " + stage2 + " \u2192 "
						+ Formatter.firstLetterUp(pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to")
								.get(0).getAsJsonObject().getAsJsonArray("evolves_to").get(0).getAsJsonObject()
								.getAsJsonObject("species").get("name").getAsString());
				for (int j = 1; j < pokemonEvolution.getAsJsonObject("chain").getAsJsonArray("evolves_to").get(0)
						.getAsJsonObject().getAsJsonArray("evolves_to").size(); j++) {
					chain = String.valueOf(chain) + "\n" + stage1 + " \u2192 " + stage2 + " \u2192 "
							+ Formatter.firstLetterUp(pokemonEvolution.getAsJsonObject("chain")
									.getAsJsonArray("evolves_to").get(0).getAsJsonObject().getAsJsonArray("evolves_to")
									.get(j).getAsJsonObject().getAsJsonObject("species").get("name").getAsString());
				}
			}
		}
		return chain;
	}

	protected boolean isUltraBeast() {
		List<String> ids = new ArrayList<>(
				List.of("793", "794", "795", "796", "797", "798", "799", "803", "804", "805", "806"));
		return ids.contains(this.pokedexId);
	}
	
	/**
	 * Method to check if two Pokémon share the same species (i.e. Pokédex id)
	 */
	public boolean isSameSpecies(PokemonSpecies p) {
		return p.pokedexId.equals(this.pokedexId);
	}
	
	public static boolean isSameSpecies(PokemonSpecies p1, PokemonSpecies p2) {
		return p1.pokedexId.equals(p2.pokedexId);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PokemonSpecies)) {
            return false;
        }
		return isSameSpecies((PokemonSpecies)o);
	}
}