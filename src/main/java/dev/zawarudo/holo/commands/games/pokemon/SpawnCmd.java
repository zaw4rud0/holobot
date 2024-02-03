package dev.zawarudo.holo.commands.games.pokemon;

import dev.zawarudo.holo.modules.pokeapi.PokeAPI;
import dev.zawarudo.holo.modules.pokeapi.model.Pokemon;
import dev.zawarudo.holo.modules.pokeapi.model.PokemonSpecies;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.exceptions.InvalidIdException;
import dev.zawarudo.holo.utils.exceptions.NotFoundException;
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
			sendToOwner(builder);
			return;
		} catch (InvalidIdException | NotFoundException ex) {
			builder.setTitle("Error");
			builder.setDescription("Pokémon not found. Please check for typos.");
			sendToOwner(builder);
			return;
		}
		manager.spawnNewPokemon(e.getChannel().getIdLong(), pokemon);
	}
}