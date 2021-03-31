package com.xharlock.otakusenpai.games.pokemon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.utils.BufferedImageOperations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PokemonTeamCmd extends Command {

	public PokemonTeamCmd(String name) {
		super(name);
		setAliases(List.of());
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		e.getMessage().delete().queue();
		
		BufferedImage img = null;
		
		try {
			img = PokemonTeam.displayRandomTeam();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		InputStream input = null;
		
		try {
			input = BufferedImageOperations.convert(img);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Random Pokémon Team");
		builder.setFooter("Invoked by " + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
		builder.setImage("attachment://pokemonteam.png");
		e.getChannel().sendFile(input, "pokemonteam.png").embed(builder.build()).queue();
	}

}
