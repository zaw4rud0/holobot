package com.xharlock.holo.games.pokemon;

import java.awt.Color;
import java.io.IOException;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.pokeapi4java.PokeAPI;
import com.xharlock.pokeapi4java.model.Pokemon;
import com.xharlock.pokeapi4java.model.PokemonSpecies;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SpawnCmd extends Command {

	public SpawnCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to spawn a Pokémon");
		setUsage(name + " [<Pokémon name or id> | random]");
		setIsGuildOnlyCommand(true);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder embed = new EmbedBuilder();
		
		PokemonSpecies species = null;
		Pokemon pokemon = null;

		try {
			if (args.length == 0 || args.length == 1 && args[0].equals("random")) {
				species = PokeAPI.getRandomPokemonSpecies();
			} else {
				species = PokeAPI.getPokemonSpecies(args[0]);
			}
			pokemon = species.getPokemon();
		} catch (IOException ex) {
			embed.setTitle("Error");
			embed.setDescription("Pokémon not found or API error. Please check for typos or try again later");
			sendToOwner(e, embed);
			return;
		}

		embed.setTitle("A wild Pokémon appeared!");
		embed.setColor(Color.RED);
		embed.setDescription("Type `" + getPrefix(e) + "catch <Pokémon Name>` to catch it!");
		embed.setImage(pokemon.sprites.other.artwork.frontDefault);
		embed.setFooter("The next Pokémon replaces this one");
		
		sendEmbed(e, embed, false, true);
	}
}
