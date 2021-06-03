package com.xharlock.holo.games.pokemon;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.misc.Emotes;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PokemonCmd extends Command {

	public PokemonCmd(String name) {
		super(name);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.GAMES);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();
		
		if (args[0].equals("spawn")) {
			Random rand = new Random();			
			Pokemon pokemon = null;
			try {
				pokemon = new Pokemon(PokeAPI.getPokemonSpecies(rand.nextInt(PokeAPI.getPokemonCount())));
			} catch (IOException ex) {
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while communicating with the API. Please try again in a few minutes!");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			}
			
			builder.setTitle("A wild " + pokemon.name + " has appeared!");
			builder.addField("Level", "" + (rand.nextInt(75) + 1), false);
			builder.setThumbnail(pokemon.sprite_front);
			Message msg = sendEmbedAndGetMessage(e, builder, false);
			msg.addReaction(Emotes.POKE_BALL.getAsReaction()).queue();
			msg.addReaction(Emotes.GREAT_BALL.getAsReaction()).queue();
			msg.addReaction(Emotes.ULTRA_BALL.getAsReaction()).queue();
			msg.addReaction(Emotes.MASTER_BALL.getAsReaction()).queue();
		}
	}

}
