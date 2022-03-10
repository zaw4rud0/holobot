package com.xharlock.holo.anime;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.misc.Emojis;
import com.xharlock.nanojikan.JikanAPI;
import com.xharlock.nanojikan.model.Manga;
import com.xharlock.nanojikan.model.MangaResult;
import com.xharlock.nanojikan.model.Nameable;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class MangaSearchCmd extends Command {

	private EventWaiter waiter;
	private List<String> numbers;
	private int id;

	public MangaSearchCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to search for a manga in the database of MyAnimeList.");
		setUsage(name + " <manga title>");
		setExample(name + " black clover");
		setAliases(List.of("ms", "manga"));
		setThumbnail("https://upload.wikimedia.org/wikipedia/commons/7/7a/MyAnimeList_Logo.png");
		setEmbedColor(new Color(46, 81, 162));
		setCommandCategory(CommandCategory.ANIME);
		this.waiter = waiter;
		
		// TODO Refactor it into a class EventWaiter
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
		sendTyping(e);

		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {
			builder.setTitle("Error");
			builder.setDescription("Please provide a title!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, embedColor);
			return;
		}

		String search = String.join(" ", args);
		List<MangaResult> results;

		try {
			results = JikanAPI.searchManga(search);
		} catch (IOException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching data from the API. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, embedColor);
			return;
		}

		deleteInvoke(e);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < results.size(); i++) {
			sb.append(numbers.get(i) + " " + results.get(i).getTitle() + " [" + results.get(i).getType() + "]\n");
		}
		String result = sb.toString();

		builder.setTitle("Manga Search Results");
		builder.setDescription(result + "\nTo select one item, please use the according reaction");
		builder.setColor(embedColor);
		
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
				displayManga(e);
			}, 5, TimeUnit.MINUTES, () -> {
				msg.delete().queue();
			});
		});
	}

	private void displayManga(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		Manga manga = null;
		try {
			manga = JikanAPI.getManga(id);
		} catch (IOException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching your manga. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, embedColor);
			return;
		}

		// Prepare fields
		String genres = "";
		for (Nameable n : manga.getGenres()) {
			genres += n.getName() + ", ";
		}
		genres = genres.substring(0, genres.length() - 2);
		int ch = manga.getChapters();
		int vol = manga.getVolumes();
		String chapters = ch != 0 ? vol != 0 ? "Vol: " + vol + "\nCh: " + ch : String.valueOf(ch) + "Ch." : "TBA";
		String malScore = manga.getScore() != 0.0 ? String.valueOf(manga.getScore()) : "N/A";
		String malRank = manga.getRank() != 0 ? String.valueOf(manga.getRank()) : "N/A";

		// Set embed
		builder.setTitle(manga.getTitle());
		builder.setThumbnail(manga.getImageUrl());
		builder.setDescription(manga.getSynopsis());
		if (manga.getTitleEnglish() != null && !manga.getTitleEnglish().equals(manga.getTitle())) {
			builder.addField("English Title", manga.getTitleEnglish(), true);
		}
		if (manga.getTitleJapanese() != null) {
			builder.addField("Japanese Title", manga.getTitleJapanese(), true);
		}
		builder.addField("Genres", genres, false);
		builder.addField("Type", manga.getType(), true);
		builder.addField("Chapters", chapters, true);
		builder.addBlankField(true);
		builder.addField("MAL Score", malScore, true);
		builder.addField("MAL Rank", malRank, true);
		builder.addBlankField(true);
		builder.addField("Link", "[MyAnimeList](" + manga.getUrl() + ")", false);
		sendEmbed(e, builder, true, embedColor);
	}
}
