package com.xharlock.otakusenpai.games.pokemon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.utils.BufferedImageOperations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PokemonTeamCmd extends Command {

	public PokemonTeamCmd(String name) {
		super(name);
		setDescription("Use this command for every Pokémon team related subcommands");
		setAliases(List.of("poketeam"));
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.GAMES);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();

		e.getChannel().sendTyping().queue();

		EmbedBuilder builder = new EmbedBuilder();
		
		if (args[0].equals("random")) {			
			InputStream input = null;
			try {
				List<Pokemon> team = PokeAPI.getRandomTeam();
				BufferedImage img = PokemonTeam.displayTeam(team, false);
				input = BufferedImageOperations.toInputStream(img);
			} catch (IOException | InterruptedException ex) {
				builder.setTitle("Error");
				builder.setDescription("Something went wrong while creating a Pokémon team. Please try again in a few minutes!");
				sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
				return;
			}

			builder.setTitle("Random Pokémon Team");
			builder.setImage("attachment://pokemonteam.png");		
			if (e.isFromGuild())
				builder.setFooter("Invoked by " + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
			e.getChannel().sendFile(input, "pokemonteam.png").embed(builder.build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.MINUTES));
			return;
		}
		
		// Add more stuff in future, like creating a custom team
		else {
			
		}
	}

	public static MessageEmbed getHelp() {
		return null;
	}

}
