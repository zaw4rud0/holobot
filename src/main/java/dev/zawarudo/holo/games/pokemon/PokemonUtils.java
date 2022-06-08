package dev.zawarudo.holo.games.pokemon;

import java.util.Locale;

public class PokemonUtils {

    /**
     * Returns the {@link PokemonType} from the given name.
     */
    public static PokemonType getType(String type) {
        return switch (type.toLowerCase(Locale.UK)) {
            case "normal" -> PokemonType.NORMAL;
            case "fire" -> PokemonType.FIRE;
            case "fighting" -> PokemonType.FIGHTING;
            case "flying" -> PokemonType.FLYING;
            case "water" -> PokemonType.WATER;
            case "grass" -> PokemonType.GRASS;
            case "electric" -> PokemonType.ELECTRIC;
            case "poison" -> PokemonType.POISON;
            case "dark" -> PokemonType.DARK;
            case "fairy" -> PokemonType.FAIRY;
            case "psychic" -> PokemonType.PSYCHIC;
            case "steel" -> PokemonType.STEEL;
            case "rock" -> PokemonType.ROCK;
            case "ground" -> PokemonType.GROUND;
            case "bug" -> PokemonType.BUG;
            case "dragon" -> PokemonType.DRAGON;
            case "ghost" -> PokemonType.GHOST;
            case "ice" -> PokemonType.ICE;
            default -> null;
        };
    }
}