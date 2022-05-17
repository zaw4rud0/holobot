package com.xharlock.holo.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.EmbedColor;
import com.xharlock.holo.misc.Emoji;
import com.xharlock.nanojikan.JikanAPI;
import com.xharlock.nanojikan.exception.APIException;
import com.xharlock.nanojikan.exception.InvalidRequestException;
import com.xharlock.nanojikan.model.Anime;
import com.xharlock.pokeapi4java.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "animesearch",
        description = "Use this command to search for an anime in the database of MyAnimeList.",
        usage = "<title>",
        example = "one piece",
        alias = {"as", "anime"},
        thumbnail = "https://upload.wikimedia.org/wikipedia/commons/7/7a/MyAnimeList_Logo.png",
        embedColor = EmbedColor.MAL,
        category = CommandCategory.ANIME)
public class AnimeSearchCmd extends AbstractCommand {

    private final EventWaiter waiter;
    private final List<Emoji> numbers;

    public AnimeSearchCmd(EventWaiter waiter) {
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
            sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, getEmbedColor());
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
            sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, getEmbedColor());
            return;
        }

        // No results found
        if (results.isEmpty()) {
            builder.setTitle("Error");
            builder.setDescription("Couldn't find any animes with your given search terms!");
            sendEmbed(e, builder, 15, TimeUnit.SECONDS, false, getEmbedColor());
            return;
        }

        deleteInvoke(e);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            sb.append(numbers.get(i).getAsText()).append(" ").append(results.get(i).getTitle()).append(" [").append(results.get(i).getType()).append("]\n");
        }
        String result = sb.toString();

        builder.setTitle("Anime Search Results");
        builder.setDescription(result + "\nTo select one item, please use the according reaction");
        builder.setColor(getEmbedColor());

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
                if (evt.getReactionEmote().getEmoji().equals(numbers.get(i).getAsDisplay())) {
                    displayAnime(results.get(i), e);
                    return true;
                }
            }
            // Wrong reaction
            return false;
        }, evt -> msg.delete().queue(), 5, TimeUnit.MINUTES, () -> msg.delete().queue());
    }

    /**
     * Adds as many number emojis as there are results
     */
    private void addReactions(int count, Message msg) {
        for (int i = 0; i < count; i++) {
            msg.addReaction(numbers.get(i).getAsDisplay()).queue(v -> {
            }, err -> {
            });
        }
    }

    private void displayAnime(Anime anime, MessageReceivedEvent e) {
        EmbedBuilder builder = new EmbedBuilder();

        // Prepare fields
        String genres = null;
        String themes = null;

        if (anime.getGenres() != null && anime.getGenres().size() != 0) {
            genres = anime.getGenres().toString().replace("[", "").replace("]", "");
        }
        if (anime.getThemes() != null && anime.getThemes().size() != 0) {
            themes = anime.getThemes().toString().replace("[", "").replace("]", "");
        }

        String episodes = anime.getEpisodes() != 0 ? String.valueOf(anime.getEpisodes()) : "TBA";
        String season = anime.getSeason() != null ? anime.getSeason() : "TBA";
        String malScore = anime.getScore() != 0.0 ? String.valueOf(anime.getScore()) : "N/A";
        String malRank = anime.getRank() != 0 ? String.valueOf(anime.getRank()) : "N/A";

        // Set embed
        builder.setTitle(anime.getTitle());
        builder.setThumbnail(anime.getImages().getJpg().getLargeImage());

        if (anime.hasSynopsis()) {
            builder.setDescription(anime.getSynopsis());
        }
        if (anime.getTitleEnglish() != null && !anime.getTitleEnglish().equals(anime.getTitle())) {
            builder.addField("English Title", anime.getTitleEnglish(), true);
        }
        if (anime.getTitleJapanese() != null) {
            builder.addField("Japanese Title", anime.getTitleJapanese(), true);
        }
        if (genres != null) {
            builder.addField("Genres", genres, false);
        }
        if (themes != null) {
            builder.addField("Themes", themes, false);
        }

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
        sendEmbed(e, builder, true, getEmbedColor());
    }
}