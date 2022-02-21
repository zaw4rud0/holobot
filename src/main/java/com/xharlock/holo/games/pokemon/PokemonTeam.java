package com.xharlock.holo.games.pokemon;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.xharlock.holo.utils.BufferedImageOps;
import com.xharlock.holo.utils.CollageMaker;
import com.xharlock.holo.utils.Formatter;

public final class PokemonTeam {

	private PokemonTeam() {
		
	}
	
	/**
	 * Display a given Pokémon team as a BufferedImage
	 */
	public static BufferedImage displayTeam(List<Pokemon> team) throws IOException {
		List<BufferedImage> pokemonImages = new ArrayList<>();

		for (Pokemon pokemon : team) {
			// No Pokémon in this slot, draw simple gray background
			if (pokemon == null) {
				pokemonImages.add(drawGray());
				continue;
			}

			BufferedImage img = null;
			BufferedImage artwork = ImageIO.read(new URL(pokemon.sprites.other.artwork.frontDefault));

			// If Pokémon doesn't have a secondary type, fill background with only 1 color
			if (pokemon.getTypes().size() != 2) {
				img = draw(artwork, getType(pokemon.getTypes().get(0)).getColor(), null, "#" + pokemon.pokedexId + " " + Formatter.capitalize(pokemon.name));
			} else {
				img = draw(artwork, getType(pokemon.getTypes().get(0)).getColor(), getType(pokemon.getTypes().get(1)).getColor(), "#" + pokemon.pokedexId + " " + Formatter.capitalize(pokemon.name));
			}
			pokemonImages.add(img);

		}
		return CollageMaker.create3x2Collage(pokemonImages);
	}

	/**
	 * Method to draw a Pokémon and its name onto a background
	 * 
	 * @param img    = Picture of the Pokémon
	 * @param color1 = Color of the first type
	 * @param color2 = Color of the second type
	 * @param name   = Name of the Pokémon and its Pok�dex id
	 * @return A BufferedImage displaying the Pok�mon with background and name
	 */
	private static BufferedImage draw(BufferedImage img, Color color1, Color color2, String name) {
		int width = 500;
		int height = 500;
		BufferedImage temp = null;
		BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = res.createGraphics();
		Color oldColor = g2.getColor();

		// Draw first type
		g2.setPaint(color1);
		g2.fillRect(0, 0, width, height);

		// Draw second type (if available)
		if (color2 != null) {
			g2.setPaint(color2);
			g2.fillPolygon(new int[] { width, 0, width }, new int[] { 0, height, height }, 3);
		}

		g2.setColor(oldColor);
		temp = BufferedImageOps.resize(img, 420, 420);
		g2.drawImage(temp, null, width / 2 - temp.getWidth() / 2, 20);
		drawName(g2, name, new Rectangle(width, height), new Font("Comic Sans MS", Font.BOLD, 30), Color.BLACK);
		g2.dispose();
		return res;
	}

	/**
	 * Helper method to draw the name of the Pok�mon in the right position
	 * 
	 * @param g2    = Graphics2D
	 * @param text  = Name and id of the Pok�mon
	 * @param rect  = A rectangle of the size of the Pok�mon image
	 * @param font  = Font of the text
	 * @param color = Color of the text
	 */
	private static void drawName(Graphics2D g2, String text, Rectangle rect, Font font, Color color) {
		FontMetrics metrics = g2.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.height - 20;
		g2.setColor(color);
		g2.setFont(font);
		g2.drawString(text, x, y);
	}

	/**
	 * Method to draw a simple gray square for when there is no Pok�mon in a slot
	 */
	private static BufferedImage drawGray() {
		int width = 500; 
		int height = 500;
		BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = res.createGraphics();
		g2.setPaint(Color.DARK_GRAY);
		g2.fillRect(0, 0, width, height);
		g2.dispose();
		return res;
	}

	private static PokemonType getType(String type) {
		switch (type.toLowerCase(Locale.UK)) {
		case "normal":
			return PokemonType.NORMAL;
		case "fire":
			return PokemonType.FIRE;
		case "fighting":
			return PokemonType.FIGHTING;
		case "flying":
			return PokemonType.FLYING;
		case "water":
			return PokemonType.WATER;
		case "grass":
			return PokemonType.GRASS;
		case "electric":
			return PokemonType.ELECTRIC;
		case "poison":
			return PokemonType.POISON;
		case "dark":
			return PokemonType.DARK;
		case "fairy":
			return PokemonType.FAIRY;
		case "psychic":
			return PokemonType.PSYCHIC;
		case "steel":
			return PokemonType.STEEL;
		case "rock":
			return PokemonType.ROCK;
		case "ground":
			return PokemonType.GROUND;
		case "bug":
			return PokemonType.BUG;
		case "dragon":
			return PokemonType.DRAGON;
		case "ghost":
			return PokemonType.GHOST;
		case "ice":
			return PokemonType.ICE;
		default:
			return null;
		}
	}
}