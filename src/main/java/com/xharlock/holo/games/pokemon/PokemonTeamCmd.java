package com.xharlock.holo.games.pokemon;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.utils.ImageOperations;
import com.xharlock.pokeapi4java.PokeAPI;
import com.xharlock.pokeapi4java.model.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Command(name = "pokemonteam",
		description = "Generates a Pokémon team",
		usage = "random",
		alias = {"poketeam"},
		guildOnly = false,
		category = CommandCategory.GAMES)
public class PokemonTeamCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();
		
		// Display help page
		if (args.length == 0) {
			e.getChannel().sendMessage("This feature is in development and thus not available yet. You probably meant `" + getPrefix(e) + "pokemonteam random`").queue();
		}
		
		else if (args[0].equals("random")) {
			sendTyping(e);
			
			InputStream input;
			
			try {
				// Generate 6 random Pokémon ids
				List<Integer> ids = new ArrayList<>();
				for (int i = 0; i < 6; i++) {
					ids.add(new Random().nextInt(PokeAPI.pokemonCount) + 1);
				}
					
				List<Pokemon> team = PokeAPI.getPokemons(ids.stream().mapToInt(k -> k).toArray());
				
				BufferedImage img = PokemonTeam.displayTeam(team);
				input = ImageOperations.toInputStream(img);
			} catch (IOException | InterruptedException ex) {
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while creating a Pokémon team. Please try again in a few minutes!");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}

			builder.setTitle("Random Pokémon Team");
			builder.setImage("attachment://pokemonteam.png");		
			if (e.isFromGuild()) {
				builder.setFooter("Invoked by " + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
			}
			e.getChannel().sendFile(input, "pokemonteam.png").setEmbeds(builder.build()).queue();
		}
		
		// Add more stuff in the future, like the ability to create a custom team for users
		else {
			e.getChannel().sendMessage("This feature is in development and thus not available yet. You probably meant `" + getPrefix(e) + "pokemonteam random`").queue();
		}
	}
}
