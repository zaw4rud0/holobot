package com.xharlock.holo.games.pokemon;

import java.io.IOException;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.pokeapi4java.PokeAPI;
import com.xharlock.pokeapi4java.exception.InvalidPokedexIdException;
import com.xharlock.pokeapi4java.exception.PokemonNotFoundException;
import com.xharlock.pokeapi4java.model.Pokemon;
import com.xharlock.pokeapi4java.model.PokemonSpecies;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SpawnCmd extends Command {

	PokemonSpawnManager manager;

	public SpawnCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to spawn a Pokémon");
		setUsage(name + " [<Pokémon name or id> | random]");
		setIsGuildOnlyCommand(true);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);

		manager = Bootstrap.holo.getPokemonSpawnManager();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();

		PokemonSpecies species = null;
		Pokemon pokemon = null;

		try {
			// Spawn random Pokémon
			if (args.length == 0 || args.length == 1 && args[0].equals("random")) {
				species = PokeAPI.getRandomPokemonSpecies();
				manager.messages.get(e.getTextChannel().getIdLong()).delete().queue();
			}
			// Make Pokémons spawn in a new text channel
			else if (args[0].equals("add")) {
				species = PokeAPI.getRandomPokemonSpecies();
			}
			// Spawn specific Pokémon
			else {
				species = PokeAPI.getPokemonSpecies(args[0]);
				manager.messages.get(e.getTextChannel().getIdLong()).delete().queue();
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
}