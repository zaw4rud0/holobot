package com.xharlock.holo.anime;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.misc.Emojis;
import com.xharlock.nanojikan.JikanAPI;
import com.xharlock.nanojikan.model.Anime;
import com.xharlock.nanojikan.model.AnimeResult;
import com.xharlock.nanojikan.model.Nameable;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class AnimeSearchCmd extends Command {

	private EventWaiter waiter;
	private List<String> numbers;
	private int id;

	public AnimeSearchCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to search for an anime");
		setUsage(name + " <anime title>");
		setExample(name + " one piece");
		setAliases(List.of("as", "anisearch", "anime"));
		setCommandCategory(CommandCategory.ANIME);
		this.waiter = waiter;
		
		numbers = Arrays.asList(
				Emojis.ONE.getAsNormal(), 
				Emojis.TWO.getAsNormal(), 
				Emojis.THREE.getAsNormal(), 
				Emojis.FOUR.getAsNormal(), 
				Emojis.FIVE.getAsNormal(), 
				Emojis.SIX.getAsNormal(), 
				Emojis.SEVEN.getAsNormal(), 
				Emojis.EIGHT.getAsNormal(), 
				Emojis.NINE.getAsNormal(), 
				Emojis.TEN.getAsNormal());
		id = -1;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild()) {
			e.getChannel().sendTyping().queue();
		}

		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {
			addErrorReaction(e.getMessage());
			builder.setTitle("Error");
			builder.setDescription("Please provide a title!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		String search = String.join(" ", args);
		List<AnimeResult> results;

		try {
			results = JikanAPI.searchAnime(search);
		} catch (IOException ex) {
			ex.printStackTrace();
			addErrorReaction(e.getMessage());
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching data from the API. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		if (e.isFromGuild()) {
			e.getMessage().delete().queue();
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < results.size(); i++) {
			sb.append(numbers.get(i) + " " + results.get(i).getTitle() + " [" + results.get(i).getType() + "]\n");
		}
		String result = sb.toString();

		builder.setTitle("Anime Search Results");
		builder.setDescription(result + "\nTo select one item, please use the according reaction");

		e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> {
			msg.addReaction(Emojis.ONE.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.TWO.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.THREE.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.FOUR.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.FIVE.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.SIX.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.SEVEN.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.EIGHT.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.NINE.getAsBrowser()).queue(v -> {}, err -> {});
			msg.addReaction(Emojis.TEN.getAsBrowser()).queue(v -> {}, err -> {});

			waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {
				// Ignore reactions on other messages
				if (evt.getMessageIdLong() != msg.getIdLong()) {
					return false;
				}

				// Ignore reactions from bots and people who didn't call this command
				if (evt.retrieveUser().complete().isBot() || !e.getAuthor().equals(evt.retrieveUser().complete())) {
					return false;
				}

				if (evt.getReactionEmote().getEmoji().equals(Emojis.ONE.getAsBrowser())) {
					id = results.get(0).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.TWO.getAsBrowser())) {
					id = results.get(1).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.THREE.getAsBrowser())) {
					id = results.get(2).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.FOUR.getAsBrowser())) {
					id = results.get(3).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.FIVE.getAsBrowser())) {
					id = results.get(4).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.SIX.getAsBrowser())) {
					id = results.get(5).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.SEVEN.getAsBrowser())) {
					id = results.get(6).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.EIGHT.getAsBrowser())) {
					id = results.get(7).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.NINE.getAsBrowser())) {
					id = results.get(8).getId();
					return true;
				}
				if (evt.getReactionEmote().getEmoji().equals(Emojis.TEN.getAsBrowser())) {
					id = results.get(9).getId();
					return true;
				}

				// Wrong reaction
				return false;
			}, evt -> {
				msg.delete().queue();
				displayAnime(e);
			}, 5, TimeUnit.MINUTES, () -> {
				msg.delete().queue();
			});
		});
	}

	private void displayAnime(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		Anime anime = null;
		try {
			anime = JikanAPI.getAnime(id);
		} catch (IOException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching your anime. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		// Prepare fields
		String genres = "";
		for (Nameable n : anime.getGenres()) {
			genres += n.getName() + ", ";
		}
		genres = genres.substring(0, genres.length() - 2);
		String episodes = anime.getEpisodes() != 0 ? String.valueOf(anime.getEpisodes()) : "TBA";
		String season = anime.getSeason() != null ? anime.getSeason() : "TBA";
		String malScore = anime.getScore() != 0.0 ? String.valueOf(anime.getScore()) : "N/A";
		String malRank = anime.getRank() != 0 ? String.valueOf(anime.getRank()) : "N/A";

		// Set embed
		builder.setTitle(anime.getTitle());
		builder.setThumbnail(anime.getImageUrl());
		builder.setDescription(anime.getSynopsis());
		if (anime.getTitleEnglish() != null && !anime.getTitleEnglish().equals(anime.getTitle()))
			builder.addField("English Title", anime.getTitleEnglish(), true);
		if (anime.getTitleJapanese() != null)
			builder.addField("Japanese Title", anime.getTitleJapanese(), true);
		builder.addField("Genres", genres, false);
		builder.addField("Type", anime.getType(), true);
		builder.addField("Episodes", episodes, true);
		builder.addField("Season", season, true);
		builder.addField("MAL Score", malScore, true);
		builder.addField("MAL Rank", malRank, true);
		builder.addBlankField(true);
		builder.addField("Link", "[MyAnimeList](" + anime.getUrl() + ")", false);
		sendEmbed(e, builder, true);
	}
}
