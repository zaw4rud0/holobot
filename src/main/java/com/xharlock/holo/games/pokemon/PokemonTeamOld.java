package com.xharlock.holo.games.pokemon;

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

import com.xharlock.holo.utils.BufferedImageOperations;
import com.xharlock.holo.utils.CollageMaker;

@Deprecated
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
