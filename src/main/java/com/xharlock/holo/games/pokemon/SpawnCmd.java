package com.xharlock.holo.games.pokemon;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.utils.ImageOperations;
import com.xharlock.pokeapi4java.PokeAPI;
import com.xharlock.pokeapi4java.exception.InvalidPokedexIdException;
import com.xharlock.pokeapi4java.exception.PokemonNotFoundException;
import com.xharlock.pokeapi4java.model.Pokemon;
import com.xharlock.pokeapi4java.model.PokemonSpecies;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Deactivated
@Command(name = "spawn",
		description = "Spawns a Pokémon",
		usage = "[<Pokémon name or id> | random]",
		ownerOnly = true,
		category = CommandCategory.GENERAL)
public class SpawnCmd extends AbstractCommand {

	private final PokemonSpawnManager manager;

	public SpawnCmd() {
		manager = Bootstrap.holo.getPokemonSpawnManager();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();

		PokemonSpecies species;
		Pokemon pokemon;

		try {
			// Spawn random Pokémon
			if (args.length == 0 || args.length == 1 && args[0].equals("random")) {
				species = PokeAPI.getRandomPokemonSpecies();
				manager.deleteMessage(e.getTextChannel().getIdLong());
			}
			// Make Pokémon spawn in a new text channel
			else if (args[0].equals("add")) {
				species = PokeAPI.getRandomPokemonSpecies();
			}
			// Spawn specific Pokémon
			else {
				species = PokeAPI.getPokemonSpecies(args[0]);
				manager.deleteMessage(e.getTextChannel().getIdLong());
			}
			pokemon = species.getPokemon();
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("API error. Please try again later.");
			sendToOwner(e, builder);
			return;
		} catch (InvalidPokedexIdException | PokemonNotFoundException ex) {
			builder.setTitle("Error");
			builder.setDescription("Pokémon not found. Please check for typos.");
			sendToOwner(e, builder);
			return;
		}
		manager.spawnNewPokemon(e.getTextChannel().getIdLong(), pokemon);
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
		BufferedImage silhouette = ImageOperations.turnBlack(pokemon);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(silhouette, 200, 50, 400, 400, null);
		g2.dispose();
		return background;
	}
}