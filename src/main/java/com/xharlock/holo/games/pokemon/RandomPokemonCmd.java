package com.xharlock.holo.games.pokemon;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RandomPokemonCmd extends Command {

	public RandomPokemonCmd(String name) {
		super(name);
		setDescription("Use this command to get a random Pokémon.");
		setUsage(name);
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.GAMES);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		Random rand = new Random();
		EmbedBuilder builder = new EmbedBuilder();
		
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		PokemonSpecies pokemon = null;
		try {
			pokemon = new PokemonSpecies(PokeAPI.getPokemonSpecies(rand.nextInt(898) + 1));
		} catch (IOException e1) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while retrieving Pokémon data. Please try again later!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
		}
		
		builder.setTitle(pokemon.name + " | " + "#" + pokemon.pokedexId);
		builder.setDescription(pokemon.genus);
		
		if (pokemon.type2 != null)
			builder.addField("Type", pokemon.type1.getEmote().getAsText() + " " + pokemon.type1.getName() + "\n" + pokemon.type2.getEmote().getAsText() + " " + pokemon.type2.getName(), false);
		else
			builder.addField("Type", pokemon.type1.getEmote().getAsText() + " " + pokemon.type1.getName(), false);
		
		builder.setThumbnail(pokemon.sprite_front);
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
	}

}
