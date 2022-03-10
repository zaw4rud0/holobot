package com.xharlock.holo.games.pokemon;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xharlock.holo.core.Bootstrap;
import com.xharlock.pokeapi4java.PokeAPI;
import com.xharlock.pokeapi4java.exception.InvalidPokedexIdException;
import com.xharlock.pokeapi4java.model.Pokemon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Class that manages where and when a {@link Pokemon} spawns.
 */
public class PokemonSpawnManager {

	private List<Long> channels;
	
	private JDA jda;
	/** {@link Pokemon}s mapped to {@link TextChannel}s */
	public Map<Long, Pokemon> pokemons;
	/** {@link Message}s containing the embed with the Pokémon in each {@link TextChannel} */
	public Map<Long, Message> messages;

	public PokemonSpawnManager(JDA jda, List<Long> channels) {
		this.jda = jda;
		this.channels = channels;
		pokemons = new HashMap<>();
		messages = new HashMap<>();
		
		spawnPokemons();
	}

	public void spawnPokemons() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("A wild Pokémon appeared!");
		builder.setColor(Color.RED);

		for (Long id : channels) {
			Pokemon pokemon = null;
			try {
				pokemon = PokeAPI.getRandomPokemon();
			} catch (IOException | InvalidPokedexIdException e) {
				e.printStackTrace();
			}

			builder.setDescription("Type `" + getPrefix(jda.getTextChannelById(id).getGuild()) + "catch <pokémon name>` to catch it!");
			builder.setImage(pokemon.sprites.other.artwork.frontDefault);

			pokemons.put(id, pokemon);
			Message msg = jda.getTextChannelById(id).sendMessageEmbeds(builder.build()).complete();
			messages.put(id, msg);
		}
	}

	public void spawnNewPokemon(long channelId) {
		Pokemon pokemon = null;
		try {
			pokemon = PokeAPI.getRandomPokemon();
		} catch (IOException | InvalidPokedexIdException e) {
			e.printStackTrace();
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("A wild Pokémon appeared!");
		builder.setColor(Color.RED);
		builder.setDescription("Type `" + getPrefix(jda.getTextChannelById(channelId).getGuild()) + "catch <pokémon name>` to catch it!");
		builder.setImage(pokemon.sprites.other.artwork.frontDefault);

		Message msg = jda.getTextChannelById(channelId).sendMessageEmbeds(builder.build()).complete();

		pokemons.put(channelId, pokemon);
		messages.put(channelId, msg);
	}

	public void spawnNewPokemon(long channelId, Pokemon pokemon) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setDescription("Type `" + getPrefix(jda.getTextChannelById(channelId).getGuild()) + "catch <pokémon name>` to catch it!");
		builder.setTitle("A wild Pokémon appeared!");
		builder.setImage(pokemon.sprites.other.artwork.frontDefault);

		Message msg = jda.getTextChannelById(channelId).sendMessageEmbeds(builder.build()).complete();

		pokemons.put(channelId, pokemon);
		messages.put(channelId, msg);
	}
	
	public void addChannel(long channelId) {
		channels.add(channelId);
		spawnNewPokemon(channelId);
	}
	
	public void removeChannel(long channelId) {
		if (!channels.contains(channelId)) {
			return;
		}
		channels.remove(channelId);
		pokemons.remove(channelId);
		messages.remove(channelId).delete().queue();
	}
	
	private String getPrefix(Guild guild) {
		return Bootstrap.holo.getGuildConfigManager().getGuildConfig(guild).getGuildPrefix();
	}
}