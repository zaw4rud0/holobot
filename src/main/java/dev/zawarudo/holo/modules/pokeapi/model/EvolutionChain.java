package dev.zawarudo.holo.modules.pokeapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Evolution chains are essentially family trees. They start with the lowest stage within a family and detail evolution conditions for each as well as Pokémon they can evolve into up through the hierarchy.
 */
class EvolutionChain {
    /**
     * The id of the evolution chain.
     */
    @SerializedName("id")
    int id;
    /**
     * The item that a Pokémon would be holding when mating that would trigger the egg hatching a baby Pokémon rather than a basic Pokémon.
     */
    @SerializedName("baby_trigger_item")
    Nameable babyTriggerItem;
    /**
     * The base chain link object. Each link contains evolution details for a Pokémon in the chain. Each link references the next Pokémon in the natural evolution order.
     */
    @SerializedName("chain")
    ChainLink chain;

    /**
     * Represents a chain link in an evolution chain. Each link contains evolution details for a Pokémon in the chain. Each link references the next Pokémon in the natural evolution order.
     */
    public static class ChainLink {
        /**
         * All details regarding the specific details of the referenced Pokémon species evolution.
         */
        @SerializedName("evolution_details")
        List<EvolutionDetails> details;
        /**
         * A List of chain objects.
         */
        @SerializedName("evolves_to")
        List<ChainLink> evolvesTo;
        /**
         * Whether this link is for a baby Pokémon. This would only ever be true on the base link.
         */
        @SerializedName("is_baby")
        boolean isBaby;
        /**
         * The Pokémon species at this point in the evolution chain.
         */
        @SerializedName("species")
        Nameable species;

        /**
         * Returns all the details regarding the specific details of the referenced Pokémon species evolution.
         */
        public List<EvolutionDetails> getDetails() {
            return details;
        }

        /**
         * Returns a list of chain objects.
         */
        public List<ChainLink> getEvolvesTo() {
            return evolvesTo;
        }

        /**
         * Returns whether this link is for a baby Pokémon. This would only ever be true on the base link.
         */
        public boolean isBaby() {
            return isBaby;
        }

        /**
         * Returns the species at this point in the evolution chain.
         */
        public Nameable getSpecies() {
            return species;
        }
    }

    /**
     * Shows all the requirements for a Pokémon to evolve
     */
    static class EvolutionDetails {
        @SerializedName("gender")
        int gender;
        @SerializedName("held_item")
        Nameable heldItem;
        @SerializedName("item")
        Nameable item;
        @SerializedName("known_move")
        Nameable knownMove;
        @SerializedName("known_move_type")
        Nameable knownMoveType;
        @SerializedName("location")
        Nameable location;
        @SerializedName("min_affection")
        int minAffection;
        @SerializedName("min_beauty")
        int minBeauty;
        @SerializedName("min_happiness")
        int minHappiness;
        @SerializedName("min_level")
        int minLevel;
        @SerializedName("needs_overworld_rain")
        boolean needsOverworldRain;
        @SerializedName("party_species")
        Nameable partySpecies;
        @SerializedName("party_type")
        Nameable partyType;
        @SerializedName("relative_physical_stats")
        int relativePhysicalStats;
        @SerializedName("time_of_day")
        String timeOfDay;
        @SerializedName("trade_species")
        Nameable tradeSpecies;
        @SerializedName("trigger")
        Nameable trigger;
        @SerializedName("turn_upside_down")
        boolean turnUpsideDown;

        /**
         * Returns the id of the gender the evolving Pokémon must have in order to evolve.
         */
        public int getGender() {
            return gender;
        }

        /**
         * Returns the item the evolving Pokémon species must be holding during the evolution trigger event.
         */
        public Nameable getHeldItem() {
            return heldItem;
        }

        /**
         * Returns the item required to trigger the evolution
         */
        public Nameable getItem() {
            return item;
        }

        /**
         * Returns the move that must be known by the evolving Pokémon species during the evolution trigger event in order to evolve into this Pokémon species.
         */
        public Nameable getKnownMove() {
            return knownMove;
        }

        /**
         * Returns the move type the evolving Pokémon species must know during the evolution trigger event in order to evolve into this Pokémon species.
         */
        public Nameable getKnownMoveType() {
            return knownMoveType;
        }

        /**
         * Returns the location the evolution must be triggered at.
         */
        public Nameable getLocation() {
            return location;
        }

        /**
         * Returns the minimum required level of affection the evolving Pokémon species to evolve into this Pokémon species.
         */
        public int getMinAffection() {
            return minAffection;
        }

        /**
         * Returns the minimum required beauty the evolving Pokémon species to evolve into this Pokémon species.
         */
        public int getMinBeauty() {
            return minBeauty;
        }

        /**
         * Returns the minimum required happiness the evolving Pokémon species to evolve into this Pokémon species.
         */
        public int getMinHappiness() {
            return minHappiness;
        }

        /**
         * Returns the minimum required level of the evolving Pokémon species to evolve into this Pokémon species.
         */
        public int getMinLevel() {
            return minLevel;
        }

        /**
         * Returns whether it must be raining in the overworld to cause evolution this Pokémon species.
         */
        public boolean needsOverworldRain() {
            return needsOverworldRain;
        }

        /**
         * Returns the Pokémon species that must be in the player's party in order for the evolving Pokémon species to evolve into this Pokémon species.
         */
        public Nameable getPartySpecies() {
            return partySpecies;
        }

        /**
         * The player must have a Pokémon of this type in their party during the evolution trigger event in order for the evolving Pokémon species to evolve into this Pokémon species.
         */
        public Nameable getPartyType() {
            return partyType;
        }

        /**
         * Returns the required relation between the Pokémon's Attack and Defense stats. 1 means Attack > Defense. 0 means Attack = Defense. -1 means Attack < Defense.
         */
        public int getRelativePhysicalStats() {
            return relativePhysicalStats;
        }

        /**
         * Returns the required time of day. Day or night.
         */
        public String getTimeOfDay() {
            return timeOfDay;
        }

        /**
         * Pokémon species for which this one must be traded.
         */
        public Nameable getTradeSpecies() {
            return tradeSpecies;
        }

        /**
         * Returns the type of event that triggers evolution into this Pokémon species.
         */
        public Nameable getTrigger() {
            return trigger;
        }

        /**
         * Returns whether the 3DS needs to be turned upside-down as this Pokémon levels up.
         */
        public boolean turnUpsideDown() {
            return turnUpsideDown;
        }
    }

    /**
     * Returns the id of the evolution chain
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the item that a Pokémon would be holding when mating that would trigger the egg hatching a baby Pokémon rather than a basic Pokémon.
     */
    public Nameable getBabyTriggerItem() {
        return babyTriggerItem;
    }

    /**
     * Returns the base chain link object. Each link contains evolution details for a Pokémon in the chain. Each link references the next Pokémon in the natural evolution order.
     */
    public ChainLink getChain() {
        return chain;
    }
}