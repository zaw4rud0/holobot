package com.xharlock.holo.anime;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.misc.Emojis;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import com.xharlock.nanojikan.JikanAPI;
import com.xharlock.nanojikan.model.Anime;

public class AnimeSearchCmd extends Command {

	private EventWaiter waiter;
	private List<String> numbers = Arrays.asList(Emojis.ONE.getAsNormal(), Emojis.TWO.getAsNormal(),
			Emojis.THREE.getAsNormal(), Emojis.FOUR.getAsNormal(), Emojis.FIVE.getAsNormal(), Emojis.SIX.getAsNormal(),
			Emojis.SEVEN.getAsNormal(), Emojis.EIGHT.getAsNormal(), Emojis.NINE.getAsNormal(),
			Emojis.TEN.getAsNormal());
	private int id = -1;

	public AnimeSearchCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to search for an anime");
		setUsage(name + " <anime title>");
		setExample(name + " one piece");
		setAliases(List.of("as", "anisearch", "anime"));
		setCommandCategory(CommandCategory.ANIME);
		this.waiter = waiter;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		
		if (args.length == 0) {
			addErrorReaction(e.getMessage());
			builder.setTitle("Error");
			builder.setDescription("Please provide an anime name!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		String search = String.join(" ", args);
		JsonArray array;

		try {
			array = JikanAPI.search(search, "anime");
		} catch (IOException ex) {
			ex.printStackTrace();
			addErrorReaction(e.getMessage());
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching data from the API. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
		if (e.isFromGuild())
			e.getMessage().delete().queue();

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.size(); i++) {
			sb.append(numbers.get(i) + " " + array.get(i).getAsJsonObject().get("title").getAsString() + " ["
					+ array.get(i).getAsJsonObject().get("type").getAsString() + "]\n");
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

			waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
				
				// So reactions on other messages are ignored
				if (evt.getMessageIdLong() != msg.getIdLong()) {
					return false;
				}
				
				if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {

					if (evt.getReactionEmote().getEmoji().equals(Emojis.ONE.getAsBrowser())) {
						this.id = array.get(0).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.TWO.getAsBrowser())) {
						this.id = array.get(1).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.THREE.getAsBrowser())) {
						this.id = array.get(2).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.FOUR.getAsBrowser())) {
						this.id = array.get(3).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.FIVE.getAsBrowser())) {
						this.id = array.get(4).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.SIX.getAsBrowser())) {
						this.id = array.get(5).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.SEVEN.getAsBrowser())) {
						this.id = array.get(6).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.EIGHT.getAsBrowser())) {
						this.id = array.get(7).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.NINE.getAsBrowser())) {
						this.id = array.get(8).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.TEN.getAsBrowser())) {
						this.id = array.get(9).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
				}
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

		builder.setTitle(anime.getTitle());
		builder.setThumbnail(anime.getImageUrl());
		builder.setDescription(anime.getSynopsis());
		if (anime.getTitleEnglish() != null && !anime.getTitleEnglish().equals(anime.getTitle())) {
			builder.addField("English Title", anime.getTitleEnglish(), true);
		}		
		if (!anime.getTitleJapanese().equals("null")) {
			builder.addField("Japanese Title", anime.getTitleJapanese(), true);
		}
		builder.addField("Genres", anime.getGenres().toString().replace("[", "").replace("]", ""), false);
		builder.addField("Type", anime.getType(), true);
		if (anime.getEpisodes() != 0) {
			builder.addField("Episodes", new StringBuilder().append(anime.getEpisodes()).toString(), true);
		} else {
			builder.addField("Episodes", "TBA", true);
		}
		if (anime.getSeason() != null) {
			builder.addField("Season", anime.getSeason(), true);
		} else {
			builder.addField("Season", "N/A", true);
		}
		if (anime.getScore() != 0.0) {
			builder.addField("MAL Score", new StringBuilder().append(anime.getScore()).toString(), true);
		} else {
			builder.addField("MAL Score", "N/A", true);
		}
		if (anime.getRank() != 0) {
			builder.addField("MAL Rank", new StringBuilder().append(anime.getRank()).toString(), true);
		} else {
			builder.addField("MAL Rank", "N/A", true);
		}
		builder.addBlankField(true);
		builder.addField("Link", "[MyAnimeList](" + anime.getUrl() + ")", false);
		sendEmbed(e, builder, true);
	}
}
