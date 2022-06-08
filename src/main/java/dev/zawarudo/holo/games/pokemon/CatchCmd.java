package dev.zawarudo.holo.games.pokemon;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.pokeapi4java.exception.PokemonNotFoundException;
import dev.zawarudo.pokeapi4java.model.Pokemon;
import dev.zawarudo.pokeapi4java.model.PokemonSpecies;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Command to catch the current Pokémon in a guild channel.
 */
@Deactivated
@Command(name = "catch",
		description = "Use this command to catch the current Pokémon of a text channel. Note that you can type either the English or German name of the Pokémon.",
		usage = "<Pokémon name>",
		category = CommandCategory.GAMES)
public class CatchCmd extends AbstractCommand {

	PokemonSpawnManager manager;
	
	public CatchCmd() {
		manager = Bootstrap.holo.getPokemonSpawnManager();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		Pokemon pokemon = manager.getPokemon(e.getTextChannel().getIdLong());

		// There are no Pokémon in this channel
		if (pokemon == null) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Error");
			builder.setDescription("There are no Pokémon in this channel!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		PokemonSpecies species;
		
		try {
			species = pokemon.getPokemonSpecies();
		} catch (IOException ex) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Error");
			builder.setDescription("API error. Please try again later");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		} catch (PokemonNotFoundException ex) {
			ex.printStackTrace();
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Internal Error");
			builder.setDescription("Uh-oh! This wasn't supposed to happen. Please submit a bug report with the name of this Pokémon and the id of the channel where this happened.");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		String guessed = String.join(" ", args).toLowerCase(Locale.UK);
		
		// Wrong name
		if (!guessed.equals(species.getName("en").toLowerCase(Locale.UK))
				&& !guessed.equals(species.getName("de").toLowerCase(Locale.UK))) {
			return;
		}
		
		Message msg = manager.getMessages().get(e.getTextChannel().getIdLong());
		EmbedBuilder builder = new EmbedBuilder(msg.getEmbeds().get(0));
		builder.clear();
		
		builder.setTitle("The wild " + species.getName("en") + " has been caught!");
		builder.setImage(pokemon.getSprites().getOther().getArtwork().getFrontDefault());
		builder.setColor(Color.RED);
		builder.setFooter(String.format("Caught by %s", e.getMember().getEffectiveName()), e.getMember().getEffectiveAvatarUrl());
		
		msg.editMessageEmbeds(builder.build()).queue(m -> m.delete().queueAfter(2, TimeUnit.MINUTES));
		manager.spawnNewPokemon(e.getTextChannel().getIdLong());
	}
}