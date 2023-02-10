package dev.zawarudo.holo.games.pokemon;

import dev.zawarudo.holo.utils.ImageOperations;
import dev.zawarudo.pokeapi4java.model.Pokemon;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * Utility class for Pokémon-related functionality.
 */
public class PokemonUtils {

    /**
     * Returns the {@link PokemonType} from the given name.
     *
     * @param type The name of the type.
     * @throws IllegalArgumentException If the given name is not a valid type name.
     */
    public static PokemonType getType(@NotNull String type) {
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
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

    public static BufferedImage drawHiddenPokemon(Pokemon pokemon) throws IOException {
        String sprite = pokemon.getSprites().getOther().getArtwork().getFrontDefault();
        BufferedImage blackPokemon = ImageOperations.turnBlack(ImageIO.read(new URL(sprite)));
        BufferedImage bg = ImageIO.read(new File("src/main/resources/pokemon/background/battle-background.png"));
        return createPokemonScene(blackPokemon, bg);
    }

    /**
     * Creates a Pokémon scene with the Pokémon silhouette and the background.
     *
     * @param pokemon = A BufferedImage of the Pokémon sprite.
     * @param background = A BufferedImage of the background.
     * @return A BufferedImage of the Pokémon scene.
     */
    public static BufferedImage createPokemonScene(BufferedImage pokemon, BufferedImage background) {
        Graphics2D g2 = background.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        BufferedImage silhouette = ImageOperations.turnBlack(pokemon);
        g2.drawImage(silhouette, 200, 50, 400, 400, null);
        g2.dispose();
        return background;
    }
}