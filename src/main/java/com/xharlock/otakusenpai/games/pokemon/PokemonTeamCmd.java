package com.xharlock.otakusenpai.games.pokemon;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.utils.BufferedImageOperations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PokemonTeamCmd extends Command {

	public PokemonTeamCmd(String name) {
		super(name);
		setDescription("Use this command to get a random Pokémon team.");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GAMES);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		e.getMessage().delete().queue();

		// TODO Rework :hahaa:

		InputStream input = null;

		try {
			BufferedImage img = null;
			img = PokemonTeam.displayRandomTeam();
			input = BufferedImageOperations.convertToInputStream(img);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Random Pokémon Team");
		builder.setFooter("Invoked by " + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
		builder.setImage("attachment://pokemonteam.png");
		e.getChannel().sendFile(input, "pokemonteam.png").embed(builder.build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.MINUTES));
	}

}
