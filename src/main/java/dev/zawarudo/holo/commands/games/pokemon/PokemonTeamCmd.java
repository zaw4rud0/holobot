package dev.zawarudo.holo.commands.games.pokemon;

import dev.zawarudo.holo.modules.pokeapi.PokeAPI;
import dev.zawarudo.holo.modules.pokeapi.model.Pokemon;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.ImageOperations;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

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
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		EmbedBuilder builder = new EmbedBuilder();
		
		// Display help page
		if (args.length == 0) {
			event.getChannel().sendMessage("This feature is in development and thus not available yet. " +
					"You probably meant `" + getPrefix(event) + "pokemonteam random`").queue();
		}
		
		else if ("random".equals(args[0])) {
			sendTyping(event);
			
			InputStream input;
			
			try {
				// Generate 6 random Pokémon ids
				List<Integer> ids = new ArrayList<>();
				for (int i = 0; i < 6; i++) {
					ids.add(new Random().nextInt(PokeAPI.pokemonCount) + 1);
				}

				List<Pokemon> pokemons = PokeAPI.getPokemon(ids.stream().mapToInt(k -> k).toArray());
				PokemonTeam team = new PokemonTeam(pokemons.toArray(new Pokemon[0]));

				BufferedImage img = team.generateTeamImage();
				input = ImageOperations.toInputStream(img);
			} catch (IOException | InterruptedException ex) {
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while creating a Pokémon team. Please try again in a few minutes!");
				sendEmbed(event, builder, false, 15, TimeUnit.SECONDS);
				return;
			}

			builder.setTitle("Random Pokémon Team");
			builder.setImage("attachment://pokemonteam.png");		
			if (event.isFromGuild()) {
				builder.setFooter("Invoked by " + event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl());
			}
			FileUpload upload = FileUpload.fromData(input, "pokemonteam.png");
			event.getChannel().sendFiles(upload).setEmbeds(builder.build()).queue();
		}
		
		// Add more stuff in the future, like the ability to create a custom team for users
		else {
			event.getChannel().sendMessage("This feature is in development and thus not available yet. You probably meant `" + getPrefix(event) + "pokemonteam random`").queue();
		}
	}
}
