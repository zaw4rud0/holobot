package com.xharlock.holo.anime;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.misc.Emojis;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import com.xharlock.nanojikan.JikanAPI;
import com.xharlock.nanojikan.model.Manga;
import com.xharlock.nanojikan.model.MangaResult;
import com.xharlock.nanojikan.model.Nameable;

public class MangaSearchCmd extends Command {

	private EventWaiter waiter;
	private List<String> numbers = Arrays.asList(Emojis.ONE.getAsNormal(), Emojis.TWO.getAsNormal(),
			Emojis.THREE.getAsNormal(), Emojis.FOUR.getAsNormal(), Emojis.FIVE.getAsNormal(), Emojis.SIX.getAsNormal(),
			Emojis.SEVEN.getAsNormal(), Emojis.EIGHT.getAsNormal(), Emojis.NINE.getAsNormal(),
			Emojis.TEN.getAsNormal());
	private int id = -1;

	public MangaSearchCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to search for a manga");
		setUsage(name + " <manga title>");
		setExample(name + " black clover");
		setAliases(List.of("ms", "manga"));
		setCommandCategory(CommandCategory.ANIME);
		this.waiter = waiter;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0) {
			addErrorReaction(e.getMessage());
			builder.setTitle("Error");
			builder.setDescription("Please provide a name!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		String search = String.join(" ", args);
		List<MangaResult> results;

		try {
			System.out.println("flag 1");
			results = JikanAPI.searchManga(search);
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

		for (int i = 0; i < results.size(); i++) {
			sb.append(numbers.get(i) + " " + results.get(i).getTitle() + " [" + results.get(i).getType() + "]\n");
		}

		String result = sb.toString();

		builder.setTitle("Manga Search Results");
		builder.setDescription(result + "\nTo select one item, please use the according reaction");

		e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> {

			msg.addReaction(Emojis.ONE.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.TWO.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.THREE.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.FOUR.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.FIVE.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.SIX.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.SEVEN.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.EIGHT.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.NINE.getAsBrowser()).queue(v -> {
			}, err -> {
			});
			msg.addReaction(Emojis.TEN.getAsBrowser()).queue(v -> {
			}, err -> {
			});

			waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {

				// So reactions on other messages are ignored
				if (evt.getMessageIdLong() != msg.getIdLong()) {
					return false;
				}

				if (!evt.retrieveUser().complete().isBot() && e.getAuthor().equals(evt.retrieveUser().complete())) {

					if (evt.getReactionEmote().getEmoji().equals(Emojis.ONE.getAsBrowser())) {
						this.id = results.get(0).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.TWO.getAsBrowser())) {
						this.id = results.get(1).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.THREE.getAsBrowser())) {
						this.id = results.get(2).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.FOUR.getAsBrowser())) {
						this.id = results.get(3).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.FIVE.getAsBrowser())) {
						this.id = results.get(4).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.SIX.getAsBrowser())) {
						this.id = results.get(5).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.SEVEN.getAsBrowser())) {
						this.id = results.get(6).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.EIGHT.getAsBrowser())) {
						this.id = results.get(7).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.NINE.getAsBrowser())) {
						this.id = results.get(8).getId();
						return true;
					}
					if (evt.getReactionEmote().getEmoji().equals(Emojis.TEN.getAsBrowser())) {
						this.id = results.get(9).getId();
						return true;
					}
				}
				return false;
			}, evt -> {
				msg.delete().queue();
				displayManga(e);
			}, 5, TimeUnit.MINUTES, () -> {
				msg.delete().queue();
			});
		});
	}

	private void displayManga(final MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		Manga manga = null;
		try {
			manga = JikanAPI.getManga(id);
		} catch (IOException ex) {
			ex.printStackTrace();
			builder.setTitle("Error");
			builder.setDescription(
					"Something went wrong while fetching your manga. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		builder.setTitle(manga.getTitle());
		builder.setThumbnail(manga.getImageUrl());
		builder.setDescription(manga.getSynopsis());
		if (manga.getTitleEnglish() != null && !manga.getTitleEnglish().equals(manga.getTitle())) {
			builder.addField("English Title", manga.getTitleEnglish(), true);
		}
		if (!manga.getTitleJapanese().equals("null")) {
			builder.addField("Japanese Title", manga.getTitleJapanese(), true);
		}

		// Append genres to a single String
		String genres = "";
		for (Nameable n : manga.getGenres()) genres += n.getName() + ", ";
		genres = genres.substring(0, genres.length() - 2);
		builder.addField("Genres", genres, false);

		builder.addField("Type", manga.getType(), true);
		if (manga.getChapters() != 0) {
			if (manga.getVolumes() != 0) {
				builder.addField("Chapters", "Vol: " + manga.getVolumes() + "\nCh: " + manga.getChapters(), true);
			} else {
				builder.addField("Chapters", String.valueOf(manga.getChapters()) + "Ch.", true);
			}
		} else {
			builder.addField("Chapters", "TBA", true);
		}
		builder.addBlankField(true);
		if (manga.getScore() != 0.0) {
			builder.addField("MAL Score", new StringBuilder().append(manga.getScore()).toString(), true);
		} else {
			builder.addField("MAL Score", "N/A", true);
		}
		if (manga.getRank() != 0) {
			builder.addField("MAL Rank", new StringBuilder().append(manga.getRank()).toString(), true);
		} else {
			builder.addField("MAL Rank", "N/A", true);
		}
		builder.addBlankField(true);
		builder.addField("Link", "[MyAnimeList](" + manga.getUrl() + ")", false);
		sendEmbed(e, builder, true);
	}
}
