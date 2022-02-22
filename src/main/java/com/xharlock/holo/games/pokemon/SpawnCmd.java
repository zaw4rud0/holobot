package com.xharlock.holo.games.pokemon;

import java.io.IOException;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.pokeapi4java.PokeAPI;
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
			}

			// Make Pokémons spawn in the TextChannel
			else if (args[0].equals("add")) {
				
				// TODO				
				
			}

			// Spawn specific Pokémon
			else {
				species = PokeAPI.getPokemonSpecies(args[0]);
			}
			pokemon = species.getPokemon();
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Pokémon not found or API error. Please check for typos or try again later");
			sendToOwner(e, builder);
			return;
		}

		manager.messages.get(e.getTextChannel().getIdLong()).delete().queue();
		manager.spawnNewPokemon(e.getTextChannel().getIdLong(), pokemon);
	}
}