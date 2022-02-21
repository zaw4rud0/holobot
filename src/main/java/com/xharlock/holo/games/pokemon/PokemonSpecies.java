package com.xharlock.holo.games.pokemon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.xharlock.holo.utils.Formatter;
import com.xharlock.holo.utils.HttpResponse;

/**
 * A Pokémon Species forms the basis for at least one Pokémon. Attributes of a
 * Pokémon species are shared across all varieties of Pokémon.
 */
public class PokemonSpecies {
	@SerializedName("name")
	public String name;
	@SerializedName("id")
	public int pokedexId;
	@SerializedName("generation")
	public Nameable generation;
	/** The chance of this Pokémon being female, in eights; or -1 for genderless */
	@SerializedName("gender_rate")
	public int genderRate;
	@SerializedName("has_gender_differences")
	public boolean hasGenderDifferences;

	@SerializedName("evolution_chain")
	public Url evolutionChain;
	@SerializedName("evolves_from_species")
	public Nameable evolvesFromSpecies;

	@SerializedName("capture_rate")
	public int captureRate;
	@SerializedName("base_happiness")
	public int baseHappiness;
	@SerializedName("habitat")
	public Nameable habitat;
	@SerializedName("growth_rate")
	public Nameable growthRate;
	@SerializedName("color")
	public Nameable color;
	@SerializedName("egg_groups")
	public List<Nameable> eggGroups;
	@SerializedName("shape")
	public Nameable shape;

	@SerializedName("is_baby")
	private boolean isBaby;
	@SerializedName("is_legendary")
	private boolean isLegendary;
	@SerializedName("is_mythical")
	private boolean isMythical;

	@SerializedName("names")
	public List<LangNameable> names;
	@SerializedName("genera")
	public List<LangNameable> genera;
	@SerializedName("flavor_text_entries")
	public List<PokedexEntry> pokedexEntries;

	@SerializedName("forms_switchable")
	public boolean formsSwitchable;
	@SerializedName("form_descriptions")
	public List<LangNameable> formDescriptions;
	@SerializedName("varieties")
	public List<Variety> varieties;

	/** A Pokédex entry in a given language and game version */
	public class PokedexEntry {
		@SerializedName("flavor_text")
		public String text;
		@SerializedName("language")
		public Nameable language;
		@SerializedName("version")
		public Nameable version;

		public String cleanText(String name) {
			return text.replace("\n", " ")
					.replace("\r", " ")
					.replace("POKéMON", "Pokémon")
					.replace("", " ")
					.replace("BERRIES", "berries")
					.replace("STONES", "stones")
					.replace("TRAINER", "trainer")
					.replace(name.toUpperCase(Locale.UK), Formatter.capitalize(name));
		}
	}

	/** TODO Documentation and a few methods */
	public class Variety {
		@SerializedName("is_default")
		public boolean isDefault;
		@SerializedName("pokemon")
		public Nameable pokemon;
	}

	/**
	 * Returns the name of the Pokémon in a given language. Note that the language
	 * is its abbreviated form, i.e. English -> en.
	 * 
	 * @param language = The language of the Pokémon name
	 * @return The Pokémon name in the given language. If not found, it returns null
	 *         instead.
	 */
	public String getName(String language) {
		for (LangNameable name : names) {
			if (name.language.name.equals(language)) {
				return name.name;
			}
		}
		return null;
	}

	/**
	 * Returns the genus of the Pokémon in a given language. Note that the language
	 * is its abbreviated form, i.e. English -> en.
	 * 
	 * @param genus = The language of the Pokémon genus
	 * @return The Pokémon genus in the given language. If not found, it returns
	 *         null instead.
	 */
	public String getGenus(String language) {
		for (LangNameable genus : genera) {
			if (genus.language.name.equals(language)) {
				return genus.name;
			}
		}
		return null;
	}

	/**
	 * Returns a random Pokédex entry of the Pokémon in a given language.
	 * 
	 * @param language = The language of the entry
	 * @return The Pokédex entry of the Pokémon in a given language.
	 */
	public String getPokedexEntry(String language) {
		List<String> list = new ArrayList<>();
		for (PokedexEntry entry : pokedexEntries) {
			if (entry.language.name.equals(language)) {
				list.add(entry.cleanText(name));
			}
		}
		return list.get(new Random().nextInt(list.size()));
	}
	
	/**
	 * Returns the Pokédex entry of the Pokémon in a given language and game
	 * version.
	 * 
	 * @param language = The language of the entry
	 * @param version  = The game version of the entry
	 * @return The Pokédex entry of the Pokémon in a given language and game
	 *         version.
	 */
	public String getPokedexEntry(String language, String version) {
		for (PokedexEntry entry : pokedexEntries) {
			if (entry.language.name.equals(language) && entry.version.name.equals(version)) {
				return entry.cleanText(name);
			}
		}
		return null;
	}

	/**
	 * Returns a list of Pokédex entries for this Pokémon in a given language.
	 * 
	 * @param language = The language the entries should be in
	 * @return List of Pokédex entries
	 */
	public List<String> getPokedexEntries(String language) {
		List<String> list = new ArrayList<>();
		for (PokedexEntry entry : pokedexEntries) {
			if (entry.language.name.equals(language)) {
				list.add(entry.cleanText(name));
			}
		}
		return list;
	}

	/**
	 * Checks whether or not this Pokémon is a baby. Baby Pokémon are at the lowest
	 * stage of Pokémon evolution and cannot breed.
	 */
	public boolean isBaby() {
		return isBaby;
	}

	/**
	 * Checks whether or not this Pokémon is a legendary Pokémon. Legendary Pokémon
	 * are a group of incredibly rare and often very powerful Pokémon, generally
	 * featured prominently in the legends and myths of the Pokémon world.
	 */
	public boolean isLegendary() {
		return isLegendary;
	}

	/**
	 * Checks whether or not this Pokémon is a mythical Pokémon. Mythical Pokémon
	 * are a group of Pokémon seen so rarely that some question their very
	 * existence.
	 */
	public boolean isMythical() {
		return isMythical;
	}

	/**
	 * Checks whether or not this Pokémon species is an Ultra Beast. The Ultra
	 * Beasts are a group of extradimensional Pokémon originating from Ultra Space.
	 */
	public boolean isUltraBeast() {
		// Ids of Ultra Beast Pokémons
		List<Integer> ids = new ArrayList<>(List.of(793, 794, 795, 796, 797, 798, 799, 803, 804, 805, 806));
		return ids.contains(pokedexId);
	}

	/**
	 * Returns an {@link EvolutionChain} object
	 * 
	 * @return {@link EvolutionChain}
	 */
	public EvolutionChain getEvolutionChain() {
		JsonObject obj = null;
		try {
			obj = HttpResponse.getJsonObject(evolutionChain.url);
		} catch (IOException e) {
			return null;
		}
		return new Gson().fromJson(obj, EvolutionChain.class);
	}

	/**
	 * Returns the evolution tree as a formatted String
	 */
	public String getEvolutionChainString() {
		String s = "";
		EvolutionChain evolution = getEvolutionChain();

		// No evolutions
		if (evolution.chain.evolvesTo.isEmpty()) {
			s = Formatter.capitalize(name);
		}

		// One evolution
		else if (evolution.chain.evolvesTo.get(0).evolvesTo.isEmpty()) {
			String stage1 = Formatter.capitalize(evolution.chain.species.name);
			s = stage1 + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(0).species.name);
			for (int i = 1; i < evolution.chain.evolvesTo.size(); i++) {
				s += "\n" + stage1 + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(i).species.name);
			}
		}

		// Two evolutions
		else {
			String stage1 = Formatter.capitalize(evolution.chain.species.name);
			// Multiple stage 2 evolution
			if (evolution.chain.evolvesTo.size() > 1) {
				s = stage1 + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(0).species.name)
						   + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(0).evolvesTo.get(0).species.name);
				for (int i = 1; i < evolution.chain.evolvesTo.size(); i++) {
					s += "\n" + stage1 + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(i).species.name)
									   + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(i).evolvesTo.get(0).species.name);
				}
			}
			// Only one stage 2 evolution
			else {
				String stage2 = Formatter.capitalize(evolution.chain.evolvesTo.get(0).species.name);
				
				s = stage1 + " → " + stage2 + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(0).evolvesTo.get(0).species.name);
				for (int i = 1; i < evolution.chain.evolvesTo.get(0).evolvesTo.size(); i++) {
					s += "\n" + stage1 + " → " + stage2 + " → " + Formatter.capitalize(evolution.chain.evolvesTo.get(0).evolvesTo.get(i).species.name);
				}
			}
		}
		return s;
	}

	/**
	 * Returns an individual Pokémon. In this case, it's the default variant.
	 */
	public Pokemon getPokemon() throws IOException {
		return PokeAPI.getPokemon(pokedexId);
	}
}
