package dev.zawarudo.holo.games.pokemon;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.pokeapi4java.PokeAPI;
import dev.zawarudo.pokeapi4java.exception.InvalidPokedexIdException;
import dev.zawarudo.pokeapi4java.exception.PokemonNotFoundException;
import dev.zawarudo.pokeapi4java.model.Pokemon;
import dev.zawarudo.pokeapi4java.model.PokemonSpecies;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Command(name = "pokedex",
		description = "Looks up a Pokémon and returns its information.",
		usage = "<Pokémon name or id>",
		alias = {"dex"},
		guildOnly = false,
		category = CommandCategory.GAMES)
public class PokedexCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		sendTyping(e);
		
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please provide a name or a Pokédex id!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		String search = String.join(" ", args);
		PokemonSpecies species;
		Pokemon pokemon;

		try {
			species = PokeAPI.getPokemonSpecies(search);
			pokemon = species.getPokemon();
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong. Please try again in a few minutes!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
			return;
		} catch (PokemonNotFoundException | InvalidPokedexIdException ex) {
			builder.setTitle("Error");
			builder.setDescription("Pokémon not found. Please check for typos or that you used the right Pokédex id!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
			return;	
		}

		// Prepare embed fields
		String name = species.getName("en");
		String gen = species.getGeneration().getName().toUpperCase(Locale.UK).replace("GENERATION-", "Gen ");
		String type;
		if (pokemon.getTypes().size() != 2) {
			type = PokemonUtils.getType(pokemon.getTypes().get(0)).getEmote().getAsText() + " " + pokemon.getTypes().get(0);
		} else {
			type = PokemonUtils.getType(pokemon.getTypes().get(0)).getEmote().getAsText() + " " + pokemon.getTypes().get(0) + "\n" + PokemonUtils.getType(pokemon.getTypes().get(1)).getEmote().getAsText() + " "	+ pokemon.getTypes().get(1);
		}
		String abilities = String.join("\n", pokemon.getAbilities());
		String genderRatio = species.getGenderRate() == -1.0 ? "Genderless" : 100 - species.getGenderRate() / 8.0 * 100 + "% \\\u2642 | " + species.getGenderRate() / 8.0 * 100 + "% \\\u2640";
		String entry = species.getPokedexEntry("en");
		String evolutionChain = species.getEvolutionChainString().equals(name) ? null : species.getEvolutionChainString().replace(name, "*" + name + "*");

		// Set embed
		builder.setTitle(name + " | " + "#" + species.getPokedexId() + " | " + gen);
		builder.setThumbnail(pokemon.getSprites().getOther().getArtwork().getFrontDefault());
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
		builder.addField("Height", pokemon.getHeight() / 100.0 + " m", true);
		builder.addField("Weight", pokemon.getWeight() / 1000.0 + " kg", true);
		builder.addField("Pokédex Entry", entry, false);
		if (evolutionChain != null) {
			builder.addField("Evolution", evolutionChain, false);
		}
		
		sendEmbed(e, builder, 2, TimeUnit.MINUTES, true, getColor(species.getColor().getName()));
	}

	private Color getColor(String color) {
		return switch (color) {
			case "red" -> Color.RED;
			case "blue" -> new Color(148, 219, 238);
			case "yellow" -> Color.YELLOW;
			case "green" -> Color.GREEN;
			case "black" -> Color.BLACK;
			case "brown" -> new Color(204, 153, 102);
			case "purple" -> new Color(193, 131, 193);
			case "gray" -> Color.GRAY;
			case "white" -> Color.WHITE;
			case "pink" -> Color.PINK;
			default -> null;
		};
	}
}