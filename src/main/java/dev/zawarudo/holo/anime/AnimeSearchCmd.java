package dev.zawarudo.holo.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.holo.misc.Emote;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.HoloUtils;
import dev.zawarudo.nanojikan.JikanAPI;
import dev.zawarudo.nanojikan.exception.APIException;
import dev.zawarudo.nanojikan.exception.InvalidRequestException;
import dev.zawarudo.nanojikan.model.Anime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A command for searching and displaying anime information from the MyAnimeList
 * database. It uses JikanAPI since the official MyAnimeList API doesn't provide
 * all the needed functionalities. See {@link JikanAPI} for more info.
 */
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
    private final List<Emote> selection = HoloUtils.getNumbers();

    public AnimeSearchCmd(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        sendTyping(event);

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a title to search for.");
            return;
        }

        String search = String.join(" ", args);

        List<Anime> result;
        try {
            result = JikanAPI.searchAnime(search);
        } catch (InvalidRequestException e) {
            sendErrorEmbed(event, "Something went wrong while searching for the anime! Please try again later.");
            logError("Invalid request: " + e.getMessage() + "! This wasn't supposed to happen!");
            return;
        } catch (APIException e) {
            sendErrorEmbed(event, "An error occurred while trying to search for the anime! Please try again later.");
            logError("An API error occurred while trying to search for the anime: " + e.getMessage() + " | Anime: " + search);
            return;
        }

        // No search results
        if (result.isEmpty()) {
            sendErrorEmbed(event, "I couldn't find any animes with your given search terms!");
            return;
        }

        deleteInvoke(event);

        EmbedBuilder builder = getResultsEmbed(result);
        Message msg = event.getChannel().sendMessageEmbeds(builder.build()).complete();
        User caller = event.getAuthor();

        HoloUtils.addReactions(msg, result.size());
        AtomicInteger selected = new AtomicInteger(-1);

        waiter.waitForEvent(
                MessageReactionAddEvent.class,
                evt -> {
                    if (evt.getMessageIdLong() != msg.getIdLong()) {
                        return false;
                    }
                    if (evt.retrieveUser().complete().isBot() || !caller.equals(evt.retrieveUser().complete())) {
                        return false;
                    }
                    for (int i = 0; i < result.size(); i++) {
                        if (evt.getReaction().getEmoji().equals(selection.get(i).getAsEmoji())) {
                            selected.set(i);
                            return true;
                        }
                    }
                    return false;
                },
                evt -> {
                    msg.delete().queue();
                    sendAnime(event, result.get(selected.get()));
                },
                5,
                TimeUnit.MINUTES,
                () -> msg.delete().queue()
        );
    }

    private EmbedBuilder getResultsEmbed(List<Anime> result) {
        List<Emote> numbers = HoloUtils.getNumbers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            Anime anime = result.get(i);
            String line = String.format("%s %s [%s]\n", numbers.get(i).getAsEmoji().getFormatted(), anime.getTitle(), anime.getType());
            sb.append(line);
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Anime Search results");
        builder.setDescription(sb + "\nTo select one item, please use the according reaction");
        builder.setColor(getEmbedColor());
        return builder;
    }

    private void sendAnime(MessageReceivedEvent event, Anime anime) {
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
        if (!"Movie".equals(anime.getType())) {
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
        sendEmbed(event, builder, true, getEmbedColor());
    }
}