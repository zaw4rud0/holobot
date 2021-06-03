package com.xharlock.holo.games.pokemon;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PokedexCmd extends Command {

	public PokedexCmd(String name) {
		super(name);
		setDescription("Use this command to look up a Pokémon");
		setUsage(name + " <Pokémon name or id>");
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.GAMES);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		EmbedBuilder builder = new EmbedBuilder();
		
		if (args.length == 0) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please provide a name!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		e.getChannel().sendTyping().queue();
		
		String search = args[0].replace("\u00e9", "e").replace(".", "-").replace(":", "-").replace("'", "")
				.replace("\\\u2640", "-f").replace("\\\u2642", "-m").replace(":female_sign", "-f")
				.replace(":male_sign:", "-m");
		
		PokemonSpecies pokemon = null;
		
		try {
			pokemon = new PokemonSpecies(PokeAPI.getPokemonSpecies(search.toLowerCase()));
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		if (pokemon.name == null) {
			builder.setTitle("Pokémon not found");
			builder.setDescription("Please check for typos and try again!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		builder.setTitle(pokemon.name + " | " + "#" + pokemon.pokedexId + " | " + pokemon.generation);
		builder.setThumbnail(pokemon.artwork);
		builder.setDescription((CharSequence) pokemon.genus);
		
		if (pokemon.type2 == null)
			builder.addField("Type", pokemon.type1.getEmote().getAsText() + " " + pokemon.type1.getName(), true);
		else
			builder.addField("Type", pokemon.type1.getEmote().getAsText() + " " + pokemon.type1.getName()
			+ "\n" + pokemon.type2.getEmote().getAsText() + " " + pokemon.type2.getName(), true);
		
		builder.addField("Ability", pokemon.abilities, true);
		
		if (pokemon.isLegendary)
			builder.addField("Category", "Legendary", true);
		else if (pokemon.isMythical)
			builder.addField("Category", "Mythical", true);
		else if (pokemon.isUltraBeast)
			builder.addField("Category", "Ultra Beast", true);
		else
			builder.addBlankField(true);
		
		String gender_ratio = "";
		
		if (pokemon.genderRate == -1.0) {
			gender_ratio = "100% \\\u26b2";
		}
		else {
			double percentage = pokemon.genderRate * 100;
			gender_ratio = 100 - percentage + "% \\\u2642 | " + percentage + "% \\\u2640";
		}
		
		builder.addField("Gender Ratio", gender_ratio , true);
		
		builder.addField("Height", pokemon.height, true);
		builder.addField("Weight", pokemon.weight, true);
		builder.addField("Pokédex Entry", pokemon.pokedexEntry, false);
		
		if (pokemon.evolutionChain != null)
			builder.addField("Evolution", pokemon.evolutionChain, false);
		
		sendEmbed(e, builder, true);
	}

}
