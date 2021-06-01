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
import java.util.Arrays;
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
	 * Method to display the current Pokémon team as a BufferedImage
	 */
	public BufferedImage displayTeam(boolean matchings) throws IllegalArgumentException, IOException {

		// Convert team array to a List
		List<Pokemon> team = Arrays.asList(this.team);

		// Types of the Pokémon in the team should match, Markbeep's code here
		// Basically reorder and rearrange the types here
		if (matchings) {
			team = match(team);
		}

		List<BufferedImage> pokemon_images = new ArrayList<>();

		for (Pokemon pokemon : team) {
			// No Pokémon in this slot, draw a simple gray background
			if (pokemon == null) {
				pokemon_images.add(drawGray());
			} else {
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
		}
		return CollageMaker.create3x2Collage(pokemon_images);
	}

	/**
	 * Method to display a given Pokémon team as a BufferedImage
	 * 
	 * @param team      = List of Pokemon to display
	 * @param matchings = Match the types of the Pokémon
	 * @return Reordered List of Pokémon
	 */
	public static BufferedImage displayTeam(List<Pokemon> team, boolean matchings) throws IllegalArgumentException, IOException {

		if (team.size() > 6)
			throw new IllegalArgumentException("The list may not contain more than 6 Pokémon!");

		// Types of the Pokémon in the team should match, Markbeep's code here
		// Basically reorder and rearrange the types here
		if (matchings) {
			team = match(team);
		}

		List<BufferedImage> pokemon_images = new ArrayList<>();

		for (Pokemon pokemon : team) {
			// No Pokémon in this slot, draw a simple gray background
			if (pokemon == null) {
				pokemon_images.add(drawGray());
			} else {
				BufferedImage img = null;
				BufferedImage artwork = ImageIO.read(new URL(pokemon.artwork));

				// If Pokémon doesn't have a secondary type, fill background with only 1 color
				if (pokemon.type2 == null)
					img = draw(artwork, pokemon.type1.getColor(), null, "#" + pokemon.pokedexId + " " + pokemon.name);
				else
					img = draw(artwork, pokemon.type1.getColor(), pokemon.type2.getColor(),
							"#" + pokemon.pokedexId + " " + pokemon.name);
				pokemon_images.add(img);
			}
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

	/**
	 * Method to draw a simple gray image for when there is no Pokémon in a slot
	 */
	private static BufferedImage drawGray() {
		int width = 500, height = 500;
		BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = res.createGraphics();
		g2.setPaint(Color.DARK_GRAY);
		g2.fillRect(0, 0, width, height);
		g2.dispose();
		return res;
	}

	
	// TODO Method that calls the code of Markbeep
	private static List<Pokemon> match(List<Pokemon> team) {
		
		return SortedPokemon.orderPokemon(team);
	}
}

// ###################################################
// Unchanged code

//Code by MarkBeep
class SortedPokemon {

	/*
	 * Orders the given Pokemon list so that
	 * the most type colors overlap
	 */
	public static List<Pokemon> orderPokemon(List<Pokemon> pokes) {
		
		List<Pokemon> doublePokes = doublePokes(pokes);
		List<Pokemon> bestTeam = new ArrayList<>();
		
		findBestMatches(doublePokes, new int[1], bestTeam, new ArrayList<>());

		return bestTeam;
	}

	/*
	 * Doubles the pokemon list by swapping the types
	 */
	public static List<Pokemon> doublePokes(List<Pokemon> pokes) {
		List<Pokemon> copies = new ArrayList<>();
		List<PokemonType> types = new ArrayList<>();
		// this first loop is simply to take note of all types
		for (Pokemon p: pokes) {
			types.add(p.type1);
			// we add the second type if its a different one
			if (!p.type1.equals(p.type2)) types.add(p.type2);
		}
		// now we can iterate through pokes and if they have unique types, don't copy them
		for (Pokemon p : pokes) {
			// iterate over the list and find multiple instances
			int t1 = 0;  // counter for occurences of first type
			int t2 = 0;  // counter for occurences of second type
			for (PokemonType s: types) {
				if (s.equals(p.type1)) t1++;
				if (s.equals(p.type2)) t2++;
			}
			// if t1 or t2 > 1, more than 1 pokemon has that type
			if (t1 > 1 || t2 > 1) {
				// Copies the pokemon and additionally swaps type1 and type2
				Pokemon copy;
				try {
					copy = p.clone();
				} catch (CloneNotSupportedException e) {
					continue;
				}
				PokemonType tmp = copy.type2;
				copy.type1 = copy.type2;
				copy.type2 = tmp;
				copies.add(copy);
			}
		}
		pokes.addAll(copies);
		return pokes;
	}

	/*
	 * Creates the array with the best matchings
	 * Works in a recursive fashion
	 */
	public static void findBestMatches(List<Pokemon> pokes, int[] best, List<Pokemon> bestTeam,
			List<Pokemon> cur) {
		
		if (cur.size() == 6) {
			// If the current array is 6 big, count the matchings
			int m = matchings(cur);
			if (best[0] <= m) {
				best[0] = m;
				bestTeam.clear();
				bestTeam.addAll(cur);
			}
			return;
		}
		for (Pokemon p : pokes) {
			if (cur.contains(p))
				continue;
			List<Pokemon> copy = new ArrayList<>(cur);
			copy.add(p);
			findBestMatches(pokes, best, bestTeam, copy);
		}
	}

	/*
	 * Counts the amount of matchings in a given list
	 */
	public static int matchings(List<Pokemon> q) {
		int t = 0;
		for (int i = 0; i < 6; i++) {
			if (i < 2) {
				// pokemon is in first or second position, so check if it matches with
				// bottom or right type
				if (q.get(i).type2.equals(q.get(i + 1).type1))
					t++;
				if (q.get(i).type2.equals(q.get(i + 3).type1))
					t++;
			} else if (i == 2) {
				// pokemon is in the third position, so check below type match
				if (q.get(i).type2.equals(q.get(i + 3).type1))
					t++;
			} else if (i < 5) {
				// pokemon is 4th and 5th position, so check type to the right
				if (q.get(i).type2.equals(q.get(i + 1).type1))
					t++;
			}
		}
		return t;
	}

}
//###################################################
