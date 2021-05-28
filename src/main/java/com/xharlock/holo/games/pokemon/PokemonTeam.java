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

import javax.imageio.ImageIO;

import com.xharlock.holo.utils.BufferedImageOperations;
import com.xharlock.holo.utils.CollageMaker;

public class PokemonTeam {

	private Pokemon[] team;

	public PokemonTeam() {
		team = new Pokemon[6];
	}

	public PokemonTeam(List<Pokemon> team) throws IllegalArgumentException {
		this.team = new Pokemon[6];
		if (team.size() > 6)
			throw new IllegalArgumentException("The list may not be bigger than 6!");
		else
			for (int i = 0; i < team.size(); i++)
				this.team[i] = team.get(i);
	}

	public PokemonTeam(Pokemon[] team) throws IllegalArgumentException {
		if (team.length > 6)
			throw new IllegalArgumentException("The array may not be bigger than 6!");
		else
			this.team = team;
	}

	public Pokemon[] getTeam() {
		return this.team;
	}

	public boolean addPokemon(Pokemon pokemon) {
		if (isFull())
			return false;
		else {
			this.team[this.team.length] = pokemon;
			return true;
		}
	}

	public boolean replace(int slot, Pokemon pokemon) {
		if (!isValidSlot(slot))
			return false;
		else {
			this.team[slot] = pokemon;
			return true;
		}
	}

	public boolean removePokemon(int slot) {
		if (isEmpty() || !isValidSlot(slot))
			return false;
		else {
			this.team[slot] = null;
			return true;
		}
	}

	public boolean swap(int slot1, int slot2) {
		if (isEmpty() || !isValidSlot(slot1) || !isValidSlot(slot2))
			return false;
		else {
			Pokemon temp = this.team[slot1];
			this.team[slot1] = this.team[slot2];
			this.team[slot2] = temp;
			return true;
		}
	}

	public boolean isEmpty() {
		return this.team.length == 0;
	}

	public boolean isFull() {
		return this.team.length == 6;
	}

	private boolean isValidSlot(int slot) {
		if (slot >= 0 && slot < 6)
			return true;
		else
			return false;
	}

	/**
	 * Method to display the given Pokémon team as a BufferedImage
	 * 
	 * @param team      = List of Pokemon to display
	 * @param matchings = Match the types of the Pokémon
	 * @return Reordered List of Pokémon
	 */
	public static BufferedImage displayTeam(List<Pokemon> team, boolean matchings) throws IOException {

		if (team.size() > 6)
			throw new IllegalArgumentException("The list may not be bigger than 6!");

		// Types of the Pokémon in the team should match, Markbeep's code here
		// Basically reorder and rearrange the types here
		if (matchings) {
			team = match(team);
		}

		List<BufferedImage> pokemon_images = new ArrayList<>();

		for (Pokemon pokemon : team) {
			BufferedImage img = null;
			BufferedImage artwork = ImageIO.read(new URL(pokemon.artwork));

			// If Pokémon doesn't have a secondary type, fill background with only 1 color
			if (pokemon.type2 == null)
				img = draw(artwork, pokemon.type1.getColor(), pokemon.type1.getColor(),
						"#" + pokemon.pokedexId + " " + pokemon.name);
			else
				img = draw(artwork, pokemon.type1.getColor(), pokemon.type2.getColor(),
						"#" + pokemon.pokedexId + " " + pokemon.name);

			pokemon_images.add(img);
		}

		return CollageMaker.create3x2Collage(pokemon_images);
	}

	/**
	 * Method to draw a Pokémon and its name onto a background
	 * 
	 * @param img    = Picture of the Pokémon
	 * @param color1 = Color of the first type
	 * @param color2 = Color of the second type
	 * @param name   = Name of the Pokémon and its Pokédex id
	 * @return A BufferedImage displaying the Pokémon with background and name
	 */
	private static BufferedImage draw(BufferedImage img, Color color1, Color color2, String name) {
		int width = 500, height = 500;
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
		temp = BufferedImageOperations.resize(img, 420, 420);
		g2.drawImage(temp, null, width / 2 - temp.getWidth() / 2, 20);
		drawName(g2, name, new Rectangle(width, height), new Font("Comic Sans MS", Font.BOLD, 30), Color.BLACK);
		g2.dispose();
		return res;
	}

	/**
	 * Helper method to draw the name of the Pokémon in the right position
	 * 
	 * @param g2    = Graphics2D
	 * @param text  = Name and id of the Pokémon
	 * @param rect  = A rectangle of the size of the Pokémon image
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

	private static List<Pokemon> match(List<Pokemon> team) {
		return null;
	}
}
