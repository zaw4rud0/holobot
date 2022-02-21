package com.xharlock.holo.games.pokemon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.xharlock.holo.utils.Formatter;

public class Pokemon {
	@SerializedName("name")
	public String name;
	@SerializedName("id")
	public int pokedexId;
	@SerializedName("species")
	public Nameable species;
	@SerializedName("types")
	private List<Type> types;
	@SerializedName("weight")
	public int weight;
	@SerializedName("height")
	public int height;
	@SerializedName("sprites")
	public Sprites sprites;
	@SerializedName("forms")
	public List<Nameable> forms;
	@SerializedName("is_default")
	public boolean isDefault;
	/** The experience gained for defeating this Pokémon */
	@SerializedName("base_experience")
	public int baseExperience;
	@SerializedName("abilities")
	public List<Ability> abilities;
	@SerializedName("moves")
	public List<Move> moves;
	@SerializedName("held_items")
	public List<HeldItem> heldItems;
	@SerializedName("stats")
	public List<Stat> stats;

	public class Type {
		@SerializedName("type")
		public Nameable type;
		@SerializedName("slot")
		public int slot;
	}

	public class Ability {
		@SerializedName("ability")
		public Nameable ability;
		@SerializedName("is_hidden")
		public boolean isHidden;
		@SerializedName("slot")
		public int slot;
	}

	public class Move {
		@SerializedName("move")
		public Nameable move;
		@SerializedName("version_group_details")
		public List<VersionDetails> details;

		public class VersionDetails {
			@SerializedName("level_learned_at")
			public int level;
			@SerializedName("move_learn_method")
			public Nameable learnMethod;
			@SerializedName("version")
			public Nameable gameVersion;
		}
	}

	public class HeldItem {
		@SerializedName("item")
		public Nameable item;
		@SerializedName("version_details")
		public List<VersionDetails> details;

		public class VersionDetails {
			@SerializedName("rarity")
			public int rarity;
			@SerializedName("version")
			public Nameable version;
		}
	}

	public class Stat {
		@SerializedName("stat")
		public Nameable stat;
		@SerializedName("base_stat")
		public int baseStat;
		@SerializedName("effort")
		public int effort;
	}

	public class Sprites {
		@SerializedName("front_default")
		public String frontDefault;
		@SerializedName("front_female")
		public String frontFemale;
		@SerializedName("front_shiny")
		public String frontShiny;
		@SerializedName("front_shiny_female")
		public String frontShinyFemale;
		@SerializedName("back_default")
		public String backDefault;
		@SerializedName("back_female")
		public String backFemale;
		@SerializedName("back_shiny")
		public String backShiny;
		@SerializedName("back_shiny_female")
		public String backShinyFemale;

		@SerializedName("other")
		public Other other;

		public class Other {
			@SerializedName("dream_world")
			public DreamWorld dreamWorld;
			@SerializedName("home")
			public Home home;
			@SerializedName("official-artwork")
			public Artwork artwork;
		}

		public class DreamWorld {
			@SerializedName("front_default")
			public String frontDefault;
			@SerializedName("front_female")
			public String frontFemale;
		}

		public class Home {
			@SerializedName("front_default")
			public String frontDefault;
			@SerializedName("front_female")
			public String frontFemale;
			@SerializedName("front_shiny")
			public String frontShiny;
			@SerializedName("front_shiny_female")
			public String frontShinyFemale;
		}

		public class Artwork {
			@SerializedName("front_default")
			public String frontDefault;
		}
	}
	
	/**
	 * Returns a list of types of this Pokémon. Note that a Pokémon can either have
	 * one or two types.
	 */
	public List<String> getTypes() {
		List<String> types = new ArrayList<>();
		for (Type t : this.types) {
			String name = Formatter.capitalize(t.type.name);
			if (t.slot == 1) {
				types.add(0, name);
			} else {
				types.add(name);
			}
		}
		return types;
	}

	/**
	 * Returns a list of abilities this Pokémon can have. Note that a Pokémon can
	 * have at most three abilities: two normal and one hidden ability. If the
	 * Pokémon has a hidden ability, it will always be in the last slot.
	 */
	public List<String> getAbilities() {
		List<String> abilities = new ArrayList<>();
		for (Ability a : this.abilities) {
			String name = Formatter.capitalize(a.ability.name);
			abilities.add(name);
		}
		return abilities;
	}

	/**
	 * Returns whether or not this Pokémon has a hidden ability.
	 */
	public boolean hasHiddenAbility() {
		for (Ability a : abilities) {
			if (a.isHidden) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an {@link URL} link to the shiny sprite of the Pokémon.
	 */
	public String getShiny() {
		return sprites.frontShiny;
	}

	/**
	 * Returns an {@link URL} link to the given form of the Pokémon.
	 */
	public String getPokemonForm(String formName) {
		for (Nameable form : forms) {
			if (form.name.equals(formName)) {
				return form.url;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link PokemonSpecies} of this Pokémon.
	 */
	public PokemonSpecies getPokemonSpecies() throws IOException {
		return PokeAPI.getPokemonSpecies(species.name);
	}
}
