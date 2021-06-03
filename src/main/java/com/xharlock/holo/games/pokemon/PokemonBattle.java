package com.xharlock.holo.games.pokemon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xharlock.holo.utils.BufferedImageOps;

public class PokemonBattle {

	public static int scale = 6;
	
	// Nintendo DS screen has a resolution of 256 x 192
	private static final int width = 256;
	private static final int height = 192;
	
	public static void main(String[] args) throws IOException {

		BufferedImage raw = ImageIO.read(new File("C:\\Users\\adria\\Desktop\\pokemon\\assets\\trainer/bucks_back_raw.png"));
		
		
		for (int i = 0; i < raw.getHeight(); i++) {
			for (int j = 0; j < raw.getWidth(); j++) {
				Color c = new Color(raw.getRGB(j, i));
				if (c.getRed() == 147 && c.getGreen() == 187 && c.getBlue() == 236) {
					
				}
			}			
		}
		
		
		for (int i = 0; i < 0; i++) {
			Pokemon pokemon = new Pokemon(PokeAPI.getPokemonSpecies(new Random().nextInt(PokeAPI.getPokemonCount()) + 1));
			BufferedImage img = createBattle(pokemon, Background.MOUNTAIN_DAY);
			ImageIO.write(BufferedImageOps.resize(img, width * scale, height * scale), "png", new File("C:/Users/adria/Desktop/test/hmrr" + i + ".png"));
		}
	}

	public static BufferedImage createBattle(Pokemon enemy, Background bg) throws IOException {
		BufferedImage background = prepare(bg);
		background = printElements(background, enemy);
		String url = enemy.sprite_front;
		BufferedImage pokemonImg = ImageIO.read(new URL(url));

		// Get outer most pixels of a sprite
		int top = 0, right = 0, bottom = 0, left = 0;
		boolean first = true;
		// Iterate from top to bottom and find top most and bottom most pixels
		for (int i = 0; i < pokemonImg.getHeight(); i++) {
			for (int j = 0; j < pokemonImg.getWidth(); j++) {
				if (!isTransparent(pokemonImg, j, i)) {
					if (first) {
						top = i;
						first = false;
					}
					bottom = i;
					break;
				}
			}
		}
		first = true;
		// Iterate from left to right and find left most and right most pixels
		for (int i = 0; i < pokemonImg.getWidth(); i++) {
			for (int j = 0; j < pokemonImg.getHeight(); j++) {
				if (!isTransparent(pokemonImg, i, j)) {
					if (first) {
						left = i;
						first = false;
					}
					right = i;
					break;
				}
			}
		}

		Graphics2D g = background.createGraphics();
		g.drawImage(pokemonImg, width/2 + 15, 10, null);
		return background;
	}

	private static boolean isTransparent(BufferedImage image, int x, int y) {
		int pixel = image.getRGB(x, y);
		return (pixel >> 24) == 0x00;
	}

	private static BufferedImage prepare(Background bg) throws IOException {
		BufferedImage background = ImageIO.read(new URL(bg.getUrl()));
		BufferedImage ground = ImageIO.read(new URL(bg.getGround().getUrl()));
		BufferedImage textbox = ImageIO.read(new File("./src/main/resources/pokemon/gui/textbox_red.png"));
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		g.setColor(Color.BLACK);
		g.fill3DRect(0, 0, width, height, false);
		g.drawImage(background, 0, 0, null);
		g.drawImage(ground, 0, 0, null);
		g.drawImage(textbox, 0, 0, null);
		return result;
	}
	
	private static BufferedImage printElements(BufferedImage image, Pokemon pokemon) throws IOException {
		Random rand = new Random();
		BufferedImage enemyBar = null;
		
		if (pokemon.genderRate == -1.0) {	
			enemyBar = ImageIO.read(new URL(Elements.ENEMY_BAR_GENDERLESS.getUrl()));
		} else {
			int prob = (int) (pokemon.genderRate * 100);			
			if (rand.nextInt(100) + 1 > prob)
				enemyBar = ImageIO.read(new URL(Elements.ENEMY_BAR_MALE.getUrl()));
			else 
				enemyBar = ImageIO.read(new URL(Elements.ENEMY_BAR_FEMALE.getUrl()));
		}
		Graphics2D g = image.createGraphics();
		g.drawImage(enemyBar, 0, 20, null);
		g.setColor(Color.BLACK);
		g.setFont(new Font("PKMN RBYGSC", Font.PLAIN, 12));
		g.drawString(pokemon.name, 3, 35);
		g.setColor(Color.DARK_GRAY);
		g.setFont(new Font("PKMN RBYGSC", Font.PLAIN, 10));
		g.drawString("A wild " + pokemon.name + " has appeared!", 14, 162);
		return image;
	}

	public enum Background {
		CAVE("https://cdn.discordapp.com/attachments/849550442560094208/849552032145866782/cave.png", Ground.ROCKY),
		FOREST_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849552048419635240/forest_day.png", Ground.GRASS_DAY),
		FOREST_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849552045547585536/forest_afternoon.png", Ground.GRASS_AFTERNOON),
		FOREST_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849552051150127134/forest_night.png", Ground.GRASS_NIGHT),
		GRASSLAND_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849552038198902804/field_day.png", Ground.FIELD_DAY),
		GRASSLAND_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849552034935734272/field_afternoon.png",	Ground.FIELD_AFTERNOON),
		GRASSLAND_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849552042136436746/field_night.png",	Ground.FIELD_NIGHT),
		INDOOR("https://cdn.discordapp.com/attachments/849550442560094208/849552077657997313/normal.png", Ground.NORMAL),
		MOUNTAIN_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849552057707003924/mountain_day.png", Ground.ROCKY),
		MOUNTAIN_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849552054941777950/mountain_afternoon.png", Ground.ROCKY),
		MOUNTAIN_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849552060222275594/mountain_night.png", Ground.ROCKY),
		OCEAN_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849552087598497832/ocean_day.png", Ground.WATER_DAY),
		OCEAN_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849552083388071936/ocean_afternoon.png",	Ground.WATER_AFTERNOON),
		OCEAN_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849552090828242974/ocean_night.png", Ground.WATER_NIGHT),
		SNOWY_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849552097539260416/snowy_day.png",	Ground.SNOW_DAY),
		SNOWY_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849552094230609930/snowy_afternoon.png", Ground.SNOW_AFTERNOON),
		SNOWY_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849552100861673502/snowy_night.png",	Ground.SNOW_NIGHT);

		private String url;
		private Ground ground;

		private Background(String url, Ground ground) {
			this.url = url;
			this.ground = ground;
		}

		public String getUrl() {
			return this.url;
		}

		public Ground getGround() {
			return this.ground;
		}
	}

	public enum Ground {
		FIELD_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849550608340221962/field_ground_day.png"),
		FIELD_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849550606761984010/field_ground_afternoon.png"),
		FIELD_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849550610482331678/field_ground_night.png"),
		GRASS_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849550614732079104/grass_ground_day.png"),
		GRASS_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849550613331443712/grass_ground_afternoon.png"),
		GRASS_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849550616703270942/grass_ground_night.png"),
		NORMAL("https://cdn.discordapp.com/attachments/849550442560094208/849550618988118026/normal_ground.png"),
		ROCKY("https://cdn.discordapp.com/attachments/849550442560094208/849550603252400148/cave_ground.png"),
		SAND_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849550622197022720/sand_ground_day.png"),
		SAND_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849550620452323338/sand_ground_afternoon.png"),
		SAND_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849550650282606645/sand_ground_night.png"),
		SNOW_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849550658713026560/snow_ground_day.png"),
		SNOW_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849550647421304852/snow_ground_afternoon.png"),
		SNOW_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849550663314702346/snow_ground_night.png"),
		WATER_DAY("https://cdn.discordapp.com/attachments/849550442560094208/849550671158050816/water_ground_day.png"),
		WATER_AFTERNOON("https://cdn.discordapp.com/attachments/849550442560094208/849550668007079966/water_ground_afternoon.png"),
		WATER_NIGHT("https://cdn.discordapp.com/attachments/849550442560094208/849550675162431498/water_ground_night.png");

		private String url;

		private Ground(String url) {
			this.url = url;
		}

		public String getUrl() {
			return this.url;
		}
	}

	public enum Elements {
		ALLY_BAR_MALE(""),
		ALLY_BAR_FEMALE(""),
		ALLY_BAR_GENDERLESS(""),
		ENEMY_BAR_MALE("https://cdn.discordapp.com/attachments/849550442560094208/849603576908742666/EnemyBarMale.png"),
		ENEMY_BAR_FEMALE("https://cdn.discordapp.com/attachments/849550442560094208/849603573041725440/EnemyBarFemale.png"),
		ENEMY_BAR_GENDERLESS("https://cdn.discordapp.com/attachments/849550442560094208/849603575070851102/EnemyBarGenderless.png"),
		;
		
		private String url;
		
		private Elements(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return this.url;
		}
	}

}