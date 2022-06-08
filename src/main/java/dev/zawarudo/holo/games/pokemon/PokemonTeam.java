package dev.zawarudo.holo.games.pokemon;

import dev.zawarudo.holo.utils.CollageMaker;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.ImageOperations;
import dev.zawarudo.pokeapi4java.model.Pokemon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

			BufferedImage img;
			BufferedImage artwork = ImageIO.read(new URL(pokemon.getSprites().getOther().getArtwork().getFrontDefault()));

			// If Pokémon doesn't have a secondary type, fill background with only 1 color
			if (pokemon.getTypes().size() != 2) {
				img = draw(artwork, PokemonUtils.getType(pokemon.getTypes().get(0)).getColor(), null, "#" + pokemon.getPokedexId() + " " + Formatter.capitalize(pokemon.getName()));
			} else {
				img = draw(artwork, PokemonUtils.getType(pokemon.getTypes().get(0)).getColor(), PokemonUtils.getType(pokemon.getTypes().get(1)).getColor(), "#" + pokemon.getPokedexId() + " " + Formatter.capitalize(pokemon.getName()));
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
		BufferedImage temp;
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
		temp = ImageOperations.resize(img, 420, 420);
		g2.drawImage(temp, null, width / 2 - temp.getWidth() / 2, 20);
		drawName(g2, name, new Rectangle(width, height), new Font("Comic Sans MS", Font.BOLD, 30));
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
	 */
	private static void drawName(Graphics2D g2, String text, Rectangle rect, Font font) {
		FontMetrics metrics = g2.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.height - 20;
		g2.setColor(Color.BLACK);
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
}