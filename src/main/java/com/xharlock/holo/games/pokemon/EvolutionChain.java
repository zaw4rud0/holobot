package com.xharlock.holo.games.pokemon;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EvolutionChain {
	@SerializedName("id")
	public int id;
	@SerializedName("baby_trigger_item")
	public Nameable babyTriggerItem;
	@SerializedName("chain")
	public EvolvesTo chain;
	
	/**
	 * Represents the next Pokémon in the evolution chain
	 */
	public class EvolvesTo {
		@SerializedName("evolution_details")
		public List<EvolutionDetails> details;
		@SerializedName("evolves_to")
		public List<EvolvesTo> evolvesTo;
		@SerializedName("is_baby")
		public boolean isBaby;
		@SerializedName("species")
		public Nameable species;
	}
	
	/**
	 * Shows all the requirements for a Pokémon to evolve
	 */
	public class EvolutionDetails {
		@SerializedName("gender")
		public int gender;
		@SerializedName("held_item")
		public Nameable heldItem;
		@SerializedName("item")
		public Nameable item;
		@SerializedName("known_move")
		public Nameable knownMove;
		@SerializedName("known_move_type")
		public Nameable knownMoveType;
		@SerializedName("location")
		public Nameable location;
		@SerializedName("min_affection")
		public int minAffection;
		@SerializedName("min_beauty")
		public int minBeauty;
		@SerializedName("min_happiness")
		public int minHappiness;
		@SerializedName("min_level")
		public int minLevel;
		@SerializedName("needs_overworld_rain")
		public boolean needsOverworldRain;
		@SerializedName("party_species")
		public Nameable partySpecies;
		@SerializedName("party_type")
		public Nameable partyType;
		@SerializedName("relative_physical_stats")
		public int relativePhysicalStats;
		@SerializedName("time_of_day")
		public String timeOfDay;
		@SerializedName("trade_species")
		public Nameable tradeSpecies;
		@SerializedName("trigger")
		public Nameable trigger;
		@SerializedName("turn_upside_down")
		public boolean turnUpsideDown;
	}
}