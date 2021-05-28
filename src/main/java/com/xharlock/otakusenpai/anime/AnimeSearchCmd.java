package com.xharlock.otakusenpai.anime;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.misc.Emojis;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

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
		if (e.isFromGuild())
			e.getMessage().delete().queue();
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

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.size(); i++)
			sb.append(numbers.get(i) + " " + array.get(i).getAsJsonObject().get("title").getAsString() + " ["
					+ array.get(i).getAsJsonObject().get("type").getAsString() + "]\n");

		String result = sb.toString();

		builder.setTitle("Anime Search Results");
		builder.setDescription(result + "\nTo select one item, please use the according reaction");

		e.getChannel().sendMessage(builder.build()).queue(msg -> {

			msg.addReaction(Emojis.ONE.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.TWO.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.THREE.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.FOUR.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.FIVE.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.SIX.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.SEVEN.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.EIGHT.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.NINE.getAsReaction()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.TEN.getAsReaction()).queue(v -> {
			}, err -> {
			});

			waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {
				if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {

					// So reactions on other messages will be ignored
					if (evt.getMessageIdLong() != msg.getIdLong()) {
						return false;
					}

					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.ONE.getAsReaction())) {
						this.id = array.get(0).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.TWO.getAsReaction())) {
						this.id = array.get(1).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.THREE.getAsReaction())) {
						this.id = array.get(2).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.FOUR.getAsReaction())) {
						this.id = array.get(3).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.FIVE.getAsReaction())) {
						this.id = array.get(4).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.SIX.getAsReaction())) {
						this.id = array.get(5).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.SEVEN.getAsReaction())) {
						this.id = array.get(6).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.EIGHT.getAsReaction())) {
						this.id = array.get(7).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.NINE.getAsReaction())) {
						this.id = array.get(8).getAsJsonObject().get("mal_id").getAsInt();
						return true;
					}
					if (evt.getReactionEmote().getAsReactionCode().equals(Emojis.TEN.getAsReaction())) {
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
			anime = new Anime(JikanAPI.getAnime(id));
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}

		builder.setTitle(anime.title);
		builder.setThumbnail(anime.image_url);
		builder.setDescription(anime.synopsis);
		if (anime.title_en != null && !anime.title_en.equals(anime.title)) {
			builder.addField("English Title", anime.title_en, true);
		}
		if (!anime.title_jp.equals("null")) {
			builder.addField("Japanese Title", anime.title_jp, true);
		}
		builder.addField("Genres", anime.genres.toString().replace("[", "").replace("]", ""), false);
		builder.addField("Type", anime.type, true);
		if (anime.episodes != 0) {
			builder.addField("Episodes", new StringBuilder().append(anime.episodes).toString(), true);
		} else {
			builder.addField("Episodes", "TBA", true);
		}
		if (anime.season != null) {
			builder.addField("Season", anime.season, true);
		} else {
			builder.addField("Season", "N/A", true);
		}
		if (anime.score != 0.0) {
			builder.addField("MAL Score", new StringBuilder().append(anime.score).toString(), true);
		} else {
			builder.addField("MAL Score", "N/A", true);
		}
		if (anime.rank != 0) {
			builder.addField("MAL Rank", new StringBuilder().append(anime.rank).toString(), true);
		} else {
			builder.addField("MAL Rank", "N/A", true);
		}
		builder.addBlankField(true);
		builder.addField("Link", "[MyAnimeList](" + anime.url + ")", false);
		sendEmbed(e, builder, true);
	}
}
