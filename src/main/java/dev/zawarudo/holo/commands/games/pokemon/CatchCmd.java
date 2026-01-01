package dev.zawarudo.holo.commands.games.pokemon;

import dev.zawarudo.holo.modules.pokemon.model.Pokemon;
import dev.zawarudo.holo.modules.pokemon.model.PokemonSpecies;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.NotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Command to catch the current Pokémon in a guild channel.
 */
@CommandInfo(name = "catch",
		description = "Use this command to catch the current Pokémon of a text channel. " +
				"Note that you can type either the English or German name of the Pokémon.",
		usage = "<Pokémon name>",
		embedColor = EmbedColor.POKEMON,
		category = CommandCategory.GAMES)
public class CatchCmd extends AbstractCommand {

	private final PokemonSpawnManager pokemonSpawnManager;
	
	public CatchCmd(PokemonSpawnManager pokemonSpawnManager) {
		this.pokemonSpawnManager = pokemonSpawnManager;
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);
		
		Pokemon pokemon = pokemonSpawnManager.getPokemon(event.getChannel().getIdLong());

		// There are no Pokémon in this channel
		if (pokemon == null) {
			sendErrorEmbed(event, "There are no Pokémon to catch in this channel!");
			return;
		}

		PokemonSpecies species;
		
		try {
			species = pokemon.getPokemonSpecies();
		} catch (IOException | APIException ex) {
			sendErrorEmbed(event, "API error. Please try again later");
			if (logger.isErrorEnabled()) {
				logger.error("There has been an API error.", ex);
			}
			return;
		} catch (NotFoundException ex) {
			sendErrorEmbed(event, "There has been an internal error that wasn't supposed to " +
					"happen. Please submit a bug report with the name of this Pokémon and the id of " +
					"the channel where this happened.");
			if (logger.isErrorEnabled()) {
				logger.error("There has been an internal error. Check for possible bug report.", ex);
			}
			return;
		}
		
		String guessed = String.join(" ", args).toLowerCase(Locale.UK);
		
		// Wrong name
		if (!guessed.equals(species.getName("en").toLowerCase(Locale.UK)) &&
				!guessed.equals(species.getName("de").toLowerCase(Locale.UK))) {
			return;
		}

		String catcher = event.getMember() != null ? event.getMember().getEffectiveName() : event.getAuthor().getName();

		Message msg = pokemonSpawnManager.getMessage(event.getChannel().getIdLong());
		msg.editMessageAttachments(new ArrayList<>()).queue();

		EmbedBuilder builder = new EmbedBuilder(msg.getEmbeds().getFirst());
		builder.clear();
		
		builder.setTitle("The wild " + species.getName("en") + " has been caught!");
		builder.setImage(pokemon.getSprites().getOther().getArtwork().getFrontDefault());
		builder.setColor(Color.RED);
		builder.setFooter(String.format("Caught by %s", catcher), event.getAuthor().getEffectiveAvatarUrl());

		msg.editMessageEmbeds(builder.build()).queue(m -> m.delete().queueAfter(2, TimeUnit.MINUTES));
		pokemonSpawnManager.spawnNewPokemon(event.getChannel().getIdLong());
	}
}