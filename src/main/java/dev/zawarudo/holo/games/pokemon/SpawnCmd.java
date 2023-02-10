package dev.zawarudo.holo.games.pokemon;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.pokeapi4java.PokeAPI;
import dev.zawarudo.pokeapi4java.exception.InvalidPokedexIdException;
import dev.zawarudo.pokeapi4java.exception.PokemonNotFoundException;
import dev.zawarudo.pokeapi4java.model.Pokemon;
import dev.zawarudo.pokeapi4java.model.PokemonSpecies;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Command(name = "spawn",
		description = "Spawns a Pokémon",
		usage = "[<Pokémon name or id> | random]",
		ownerOnly = true,
		category = CommandCategory.GAMES)
public class SpawnCmd extends AbstractCommand {

	private final PokemonSpawnManager manager;

	public SpawnCmd() {
		manager = Bootstrap.holo.getPokemonSpawnManager();
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();

		PokemonSpecies species;
		Pokemon pokemon;

		try {
			// Spawn random Pokémon
			if (args.length == 0 || args.length == 1 && "random".equals(args[0])) {
				species = PokeAPI.getRandomPokemonSpecies();
				manager.deleteMessage(e.getChannel().getIdLong());
			}
			// Make Pokémon spawn in a new text channel
			else if ("add".equals(args[0])) {
				species = PokeAPI.getRandomPokemonSpecies();
			}
			// Spawn specific Pokémon
			else {
				species = PokeAPI.getPokemonSpecies(args[0]);
				manager.deleteMessage(e.getChannel().getIdLong());
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
		manager.spawnNewPokemon(e.getChannel().getIdLong(), pokemon);
	}
}