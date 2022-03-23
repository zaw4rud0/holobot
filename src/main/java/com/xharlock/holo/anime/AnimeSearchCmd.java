package com.xharlock.holo.anime;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.misc.Emoji;
import com.xharlock.nanojikan.JikanAPI;
import com.xharlock.nanojikan.exception.APIException;
import com.xharlock.nanojikan.exception.InvalidRequestException;
import com.xharlock.nanojikan.model.Anime;
import com.xharlock.nanojikan.model.Nameable;
import com.xharlock.pokeapi4java.utils.Formatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class AnimeSearchCmd extends Command {

	private EventWaiter waiter;
	private List<Emoji> numbers;

	public AnimeSearchCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to search for an anime in the database of MyAnimeList.");
		setUsage(name + " <anime title>");
		setExample(name + " one piece");
		setAliases(List.of("as", "anisearch", "anime"));
		setThumbnail("https://upload.wikimedia.org/wikipedia/commons/7/7a/MyAnimeList_Logo.png");
		setEmbedColor(new Color(46, 81, 162));
		setCommandCategory(CommandCategory.ANIME);
		this.waiter = waiter;

		numbers = Arrays.asList(Emoji.ONE, Emoji.TWO, Emoji.THREE, Emoji.FOUR, Emoji.FIVE, Emoji.SIX, Emoji.SEVEN, Emoji.EIGHT, Emoji.NINE, Emoji.TEN);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		sendTyping(e);

		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {
			builder.setTitle("Error");
			builder.setDescription("Please provide a title!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, embedColor);
			return;
		}

		String search = String.join(" ", args);
		List<Anime> results;

		try {
			results = JikanAPI.searchAnime(search);
		} catch (InvalidRequestException ex) {
			System.out.println("This shouldn't have happened!");
			return;
		} catch (APIException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching data from the API. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, embedColor);
			return;
		}

		// No results found
		if (results.isEmpty()) {
			builder.setTitle("Error");
			builder.setDescription("Couldn't find any animes with your given search terms!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, embedColor);
			return;
		}

		deleteInvoke(e);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < results.size(); i++) {
			sb.append(numbers.get(i).getAsNormal() + " " + results.get(i).getTitle() + " [" + results.get(i).getType() + "]\n");
		}
		String result = sb.toString();

		builder.setTitle("Anime Search Results");
		builder.setDescription(result + "\nTo select one item, please use the according reaction");
		builder.setColor(embedColor);

		Message msg = e.getChannel().sendMessageEmbeds(builder.build()).complete();
		addReactions(results.size(), msg);

		waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {
			// Ignore reactions on other messages
			if (evt.getMessageIdLong() != msg.getIdLong()) {
				return false;
			}

			// Ignore reactions from bots and people who didn't call this command
			if (evt.retrieveUser().complete().isBot() || !e.getAuthor().equals(evt.retrieveUser().complete())) {
				return false;
			}

			for (int i = 0; i < results.size(); i++) {
				if (evt.getReactionEmote().getEmoji().equals(numbers.get(i).getAsBrowser())) {
					displayAnime(results.get(i), e);
					return true;
				}
			}
			// Wrong reaction
			return false;
		}, evt -> {
			msg.delete().queue();
		}, 5, TimeUnit.MINUTES, () -> {
			msg.delete().queue();
		});
	}

	/** Adds as many number emojis as there are results */
	private void addReactions(int count, Message msg) {
		for (int i = 0; i < count; i++) {
			msg.addReaction(numbers.get(i).getAsBrowser()).queue(v -> {}, err -> {});
		}
	}

	private void displayAnime(Anime anime, MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		
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
		builder.setThumbnail(anime.getImages().jpg.largeImageUrl);
		
		if (anime.hasSynopsis()) {
			builder.setDescription(anime.getSynopsis());
		}
		if (anime.getTitleEnglish() != null && !anime.getTitleEnglish().equals(anime.getTitle())) {
			builder.addField("English Title", anime.getTitleEnglish(), true);
		}
		if (anime.getTitleJapanese() != null) {
			builder.addField("Japanese Title", anime.getTitleJapanese(), true);
		}
		builder.addField("Genres", genres, false);
		builder.addField("Type", anime.getType(), true);
		if (!anime.getType().equals("Movie")) {
			builder.addField("Episodes", episodes, true);
			builder.addField("Season", Formatter.capitalize(season), true);
		} else {
			builder.addField("Season", Formatter.capitalize(season), true);
			builder.addBlankField(true);
		}
		builder.addField("MAL Score", malScore, true);
		builder.addField("MAL Rank", malRank, true);
		builder.addBlankField(true);
		builder.addField("Link", "[MyAnimeList](" + anime.getUrl() + ")", false);
		sendEmbed(e, builder, true, embedColor);
	}
}