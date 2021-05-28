package com.xharlock.otakusenpai.games.pokemon;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xharlock.otakusenpai.utils.BufferedImageOperations;
import com.xharlock.otakusenpai.utils.CollageMaker;
import com.xharlock.otakusenpai.utils.ImageDownloader;

public class PokemonTeamOld {

	/**
	 * Method to display six random Pok�mon on an image
	 * 
	 * @return An image of the team
	 * @throws Exception
	 */
	public static BufferedImage displayRandomTeam() throws Exception {
		Random rand = new Random();

		List<Thread> threads = new ArrayList<>();
		List<PokemonFetcherOld> fetchers = new ArrayList<>();
		
		for (int i = 0; i < 6; i++) {
			int id = rand.nextInt(PokeAPI.getPokemonCount()) + 1;			
			PokemonFetcherOld fetcher = new PokemonFetcherOld(id);
			Thread t = new Thread(fetcher);
			fetchers.add(fetcher);
			threads.add(t);
			t.start();
		}

		List<SortedPokemon> sortedPokemonList = new ArrayList<>();
		List<PokemonSpecies> allPokemon = new ArrayList<>();

		for (int i = 0; i < 6; i++) {
			try {
				threads.get(i).join();
				PokemonSpecies pokemon = fetchers.get(i).pokemon;

				allPokemon.add(pokemon);

				String type1 = pokemon.type1.getName();
				String type2 = (pokemon.type2 == null) ? type1 : pokemon.type2.getName();
				sortedPokemonList.add(new SortedPokemon(pokemon.name, type1, type2, i));

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<BufferedImage> images = SortedPokemon.orderImages(sortedPokemonList, allPokemon);

		return CollageMaker.create3x2Collage(images);
	}

	static BufferedImage draw(BufferedImage img, Color color1, Color color2, String name) {
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

	private static void drawName(Graphics2D g2, String text, Rectangle rect, Font font, Color color) {
		FontMetrics metrics = g2.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.height - 20;
		g2.setColor(color);
		g2.setFont(font);
		g2.drawString(text, x, y);
	}
}

class PokemonFetcherOld implements Runnable {
	int id;
	PokemonSpecies pokemon;

	public PokemonFetcherOld(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		try {
			this.pokemon = new PokemonSpecies(PokeAPI.getPokemonSpecies(id));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

// Code by MarkBeep

class SortedPokemon {
	String name;
	String type1;
	String type2;
	int id;

	boolean swapped;

	SortedPokemon(String name, String type1, String type2, int id) {
		this.name = name;
		this.type1 = type1;
		this.type2 = type2;
		this.id = id;
	}

	public static List<BufferedImage> orderImages(List<SortedPokemon> pokes, List<PokemonSpecies> allPokemon)
			throws Exception {
		
		List<SortedPokemon> doublePokes = doublePokes(pokes);
		List<SortedPokemon> bestTeam = new ArrayList<>();
		
		findBestMatches(doublePokes, new int[1], bestTeam, new ArrayList<>());

		// creates the image
		List<BufferedImage> ordered = new ArrayList<>();
		
		for (SortedPokemon p : bestTeam) {

			BufferedImage img = null;
			PokemonSpecies pokemon = allPokemon.get(p.id);

			PokemonType type1 = null;
			PokemonType type2 = null;

			// We set type1 and type2 correctly for the draw function
			if (!p.swapped) {
				type1 = pokemon.type1;
				type2 = (pokemon.type2 == null) ? type1 : pokemon.type2;
			} else {
				  type2 = pokemon.type1;
				  type1 = (pokemon.type2==null) ? type2: pokemon.type2;
			}

			img = PokemonTeamOld.draw(ImageDownloader.downloadBufferedImage(pokemon.artwork), type1.getColor(),
					type2.getColor(), "#" + pokemon.pokedexId + " " + pokemon.name);

			ordered.add(img);
		}
		return ordered;
	}

	/*
	 * Doubles the pokemon list by swapping the types
	 */
	public static List<SortedPokemon> doublePokes(List<SortedPokemon> pokes) {
		List<SortedPokemon> copies = new ArrayList<>();
		List<String> types = new ArrayList<>();
		// this first loop is simply to take note of all types
		for (SortedPokemon p: pokes) {
			types.add(p.type1);
			// we add the second type if its a different one
			if (!p.type1.equals(p.type2)) types.add(p.type2);
		}
		// now we can iterate through pokes and if they have unique types, don't copy them
		for (SortedPokemon p : pokes) {
			// iterate over the list and find multiple instances
			int t1 = 0;  // counter for occurences of first type
			int t2 = 0;  // counter for occurences of second type
			for (String s: types) {
				if (s.equals(p.type1)) t1++;
				if (s.equals(p.type2)) t2++;
			}
			// if t1 or t2 > 1, more than 1 pokemon has that type
			if (t1 > 1 || t2 > 1) copies.add(p.copy());
		}
		pokes.addAll(copies);
		return pokes;
	}

	/*
	 * Creates the array with the best matchings
	 */
	public static void findBestMatches(List<SortedPokemon> pokes, int[] best, List<SortedPokemon> bestTeam,
			List<SortedPokemon> cur) {
		
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
		for (SortedPokemon p : pokes) {
			if (cur.contains(p))
				continue;
			List<SortedPokemon> copy = new ArrayList<>(cur);
			copy.add(p);
			findBestMatches(pokes, best, bestTeam, copy);
		}
	}

	/*
	 * Counts the amount of matchings in a given list
	 */
	public static int matchings(List<SortedPokemon> q) {
		int t = 0;
		for (int i = 0; i < 6; i++) {
			if (i < 2) {
				if (q.get(i).type2 == q.get(i + 1).type1)
					t++;
				if (q.get(i).type2 == q.get(i + 3).type1)
					t++;
			} else if (i == 2) {
				if (q.get(i).type2 == q.get(i + 3).type1)
					t++;
			} else if (i < 5) {
				if (q.get(i).type2 == q.get(i + 1).type1)
					t++;
			}
		}
		return t;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SortedPokemon) {
			SortedPokemon p = (SortedPokemon) o;
			return p.id == this.id;
		}
		return false;
	}

	/*
	 * Returns a copied version of a pokemon with 'swapped'
	 * set to true
	 */
	public SortedPokemon copy() {
		SortedPokemon p = new SortedPokemon(name, type2, type1, id);
		p.swapped = true;
		return p;
	}
}