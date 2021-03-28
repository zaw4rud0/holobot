package com.xharlock.otakusenpai.games.pokemon;

import com.xharlock.otakusenpai.commands.core.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PokedexCmd extends Command {

	public PokedexCmd(String name) {
		super(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		String[] args = this.getArgs();
		
		if (args.length == 0) {
			this.addErrorReaction(e.getMessage());
			this.errorEmbed(e, "Incorrect Usage", "Please provide a Pok\u00e9mon name", "pokedex");
			return;
		}
		String search = args[0].replace("\u00e9", "e").replace(".", "-").replace(":", "-").replace("'", "")
				.replace("\\\u2640", "-f").replace("\\\u2642", "-m").replace(":female_sign", "-f")
				.replace(":male_sign:", "-m");
		PokemonSpecies pokemon = new PokemonSpecies(PokeAPI.getPokemonSpecies(search.toLowerCase()));
		if (pokemon.name == null) {
			this.addErrorReaction(e.getMessage());
			this.errorEmbed(e, "Pok\u00e9mon not found", "Please check for typos and try again!", "pokedex");
			return;
		}
		if (e.isFromGuild()) {
			e.getMessage().delete().queue();
		}
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle(pokemon.name + " | " + "#" + pokemon.pokedexId + " | " + pokemon.generation);
		builder.setThumbnail(pokemon.artwork);
		builder.setDescription((CharSequence) pokemon.genus);
		builder.addField("Type", pokemon.type, true);
		builder.addField("Ability", pokemon.abilities, true);
		
		if (pokemon.isLegendary) {
			builder.addField("Category", "Legendary", true);
		} else if (pokemon.isMythical) {
			builder.addField("Category", "Mythical", true);
		} else if (pokemon.isUltraBeast) {
			builder.addField("Category", "Ultra Beast", true);
		} else {
			builder.addBlankField(true);
		}
		
		builder.addField("Gender Ratio", pokemon.genderRate, true);
		builder.addField("Height", pokemon.height, true);
		builder.addField("Weight", pokemon.weight, true);
		builder.addField("Pok\u00e9dex Entry", pokemon.pokedexEntry, false);
		if (pokemon.evolutionChain != null) {
			builder.addField("Evolution", pokemon.evolutionChain, false);
		}
		this.sendEmbed(e, builder, true);
	}

}
