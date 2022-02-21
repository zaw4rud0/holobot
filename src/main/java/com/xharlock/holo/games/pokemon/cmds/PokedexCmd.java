package com.xharlock.holo.games.pokemon.cmds;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.games.pokemon.PokeAPI;
import com.xharlock.holo.games.pokemon.Pokemon;
import com.xharlock.holo.games.pokemon.PokemonSpecies;
import com.xharlock.holo.games.pokemon.PokemonType;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PokedexCmd extends Command {

	public PokedexCmd(String name) {
		super(name);
		setDescription("Use this command to look up a Pokémon");
		setUsage(name + " <Pokémon name or id> [<form name>]");
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

		sendTyping(e);

		String search = String.join(" ", args);
		PokemonSpecies species = null;
		Pokemon pokemon = null;

		try {
			species = PokeAPI.getPokemonSpecies(search);
			pokemon = species.getPokemon();
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		// No results, probably a typo
		if (species.name == null) {
			builder.setTitle("Pokémon not found");
			builder.setDescription("Please check for typos and try again!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		deleteInvoke(e);

		// Prepare embed fields
		String name = species.getName("en");
		String gen = species.generation.name.toUpperCase(Locale.UK).replace("GENERATION-", "Gen ");
		String type = "";
		if (pokemon.getTypes().size() != 2) {
			type = getType(pokemon.getTypes().get(0)).getEmote().getAsText() + " " + pokemon.getTypes().get(0);
		} else {
			type = getType(pokemon.getTypes().get(0)).getEmote().getAsText() + " " + pokemon.getTypes().get(0) + "\n" + getType(pokemon.getTypes().get(1)).getEmote().getAsText() + " "
					+ pokemon.getTypes().get(1);
		}
		String abilities = String.join("\n", pokemon.getAbilities());
		String genderRatio = species.genderRate == -1.0 ? "100% \u26b2" : 100 - species.genderRate / 8.0 * 100 + "% \\\u2642 | " + species.genderRate / 8.0 * 100 + "% \\\u2640";
		String entry = species.getPokedexEntry("en");
		String evolutionChain = species.getEvolutionChainString().equals(name) ? null : species.getEvolutionChainString().replace(name, "*" + name + "*");

		// Set embed
		builder.setTitle(name + " | " + "#" + species.pokedexId + " | " + gen);
		builder.setThumbnail(pokemon.sprites.other.artwork.frontDefault);
		builder.setColor(getColor(species.color.name));
		builder.setDescription(species.getGenus("en"));
		builder.addField("Type", type, true);
		builder.addField("Ability", abilities, true);
		if (species.isLegendary()) {
			builder.addField("Category", "Legendary", true);
		} else if (species.isMythical()) {
			builder.addField("Category", "Mythical", true);
		} else if (species.isUltraBeast()) {
			builder.addField("Category", "Ultra Beast", true);
		} else {
			builder.addBlankField(true);
		}
		builder.addField("Gender Ratio", genderRatio, true);
		builder.addField("Height", pokemon.height / 10.0 + " m", true);
		builder.addField("Weight", pokemon.weight / 10.0 + " kg", true);
		builder.addField("Pokédex Entry", entry, false);
		if (evolutionChain != null) {
			builder.addField("Evolution", evolutionChain, false);
		}
		sendEmbed(e, builder, true, true);
	}

	private Color getColor(String color) {
		switch (color) {
		case "red":
			return Color.RED;
		case "blue":
			return new Color(148, 219, 238);
		case "yellow":
			return Color.YELLOW;
		case "green":
			return Color.GREEN;
		case "black":
			return Color.BLACK;
		case "brown":
			return new Color(204, 153, 102);
		case "purple":
			return new Color(193, 131, 193);
		case "gray":
			return Color.GRAY;
		case "white":
			return Color.WHITE;
		case "pink":
			return Color.PINK;
		default:
			return null;
		}
	}

	private static PokemonType getType(String type) {
		switch (type.toLowerCase(Locale.UK)) {
		case "normal":
			return PokemonType.NORMAL;
		case "fire":
			return PokemonType.FIRE;
		case "fighting":
			return PokemonType.FIGHTING;
		case "flying":
			return PokemonType.FLYING;
		case "water":
			return PokemonType.WATER;
		case "grass":
			return PokemonType.GRASS;
		case "electric":
			return PokemonType.ELECTRIC;
		case "poison":
			return PokemonType.POISON;
		case "dark":
			return PokemonType.DARK;
		case "fairy":
			return PokemonType.FAIRY;
		case "psychic":
			return PokemonType.PSYCHIC;
		case "steel":
			return PokemonType.STEEL;
		case "rock":
			return PokemonType.ROCK;
		case "ground":
			return PokemonType.GROUND;
		case "bug":
			return PokemonType.BUG;
		case "dragon":
			return PokemonType.DRAGON;
		case "ghost":
			return PokemonType.GHOST;
		case "ice":
			return PokemonType.ICE;
		default:
			return null;
		}
	}
}
