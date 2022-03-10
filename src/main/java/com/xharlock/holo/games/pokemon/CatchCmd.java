package com.xharlock.holo.games.pokemon;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.pokeapi4java.exception.PokemonNotFoundException;
import com.xharlock.pokeapi4java.model.Pokemon;
import com.xharlock.pokeapi4java.model.PokemonSpecies;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to catch the current Pokémon in a guild channel.
 */
public class CatchCmd extends Command {

	PokemonSpawnManager manager;
	
	public CatchCmd(String name) {
		super(name);
		setDescription("Use this command to catch the current Pokémon of a textchannel. "
				+ "Note that you can type either the English or German name of the Pokémon.");
		setUsage(name + " <pokemon name>");
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GAMES);
		
		manager = Bootstrap.holo.getPokemonSpawnManager();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		Pokemon pokemon = getCurrentPokemon(e.getTextChannel().getIdLong());
		PokemonSpecies species = null;
		
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
		}
		
		String guessed = String.join(" ", args).toLowerCase(Locale.UK);
		
		// Wrong name
		if (!guessed.equals(species.getName("en").toLowerCase(Locale.UK)) && !guessed.equals(species.getName("de").toLowerCase(Locale.UK))) {
			return;
		}
		
		Message msg = manager.messages.get(e.getTextChannel().getIdLong());
		EmbedBuilder builder = new EmbedBuilder(msg.getEmbeds().get(0));
		builder.clear();
		
		builder.setTitle("The wild " + species.getName("en") + " has been caught!");
		builder.setImage(pokemon.sprites.other.artwork.frontDefault);
		builder.setColor(Color.RED);
		builder.setFooter(String.format("Caught by %s", e.getMember().getEffectiveName()), e.getAuthor().getEffectiveAvatarUrl());
		
		msg.editMessageEmbeds(builder.build()).queue(m -> m.delete().queueAfter(2, TimeUnit.MINUTES));
		manager.spawnNewPokemon(e.getTextChannel().getIdLong());
	}
	
	/**
	 * Returns the current Pokémon of a channel. Returns null if the given channel doesn't have a Pokémon.
	 */
	private Pokemon getCurrentPokemon(long channelId) {
		return manager.pokemons.get(channelId);
	}
}