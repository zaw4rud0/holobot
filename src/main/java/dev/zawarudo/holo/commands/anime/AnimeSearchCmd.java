package dev.zawarudo.holo.commands.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.modules.jikan.JikanAPI;
import dev.zawarudo.holo.modules.jikan.model.Anime;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.core.misc.Emote;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.HoloUtils;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

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
public class AnimeSearchCmd extends BaseSearchCmd<Anime> {

    public AnimeSearchCmd(EventWaiter waiter) {
        super(waiter);
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        sendTyping(event);

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a title to search for.");
            return;
        }

        String search = String.join(" ", args);
        List<Anime> result = performSearch(event, search);

        if (result.isEmpty()) {
            sendErrorEmbed(event, "I couldn't find any animes with your given search terms!");
            return;
        }

        deleteInvoke(event);
        showSearchResults(event, result);
    }

    @Override
    protected List<Anime> performSearch(MessageReceivedEvent event, String search) {
        try {
            return JikanAPI.searchAnime(search);
        } catch (InvalidRequestException ex) {
            sendErrorEmbed(event, "An error occurred while trying to search for the anime! Please try again later.");
            if (logger.isErrorEnabled()) {
                logger.error("Invalid request! This wasn't supposed to happen!", ex);
            }
        } catch (APIException ex) {
            sendErrorEmbed(event, "An error occurred while trying to search for the anime! Please try again later.");
            if (logger.isErrorEnabled()) {
                logger.error("An API error occurred while trying to search for the anime. Anime: " + search, ex);
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected EmbedBuilder createSearchResultEmbed(List<Anime> results) {
        List<Emote> numbers = HoloUtils.getNumbers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            Anime anime = results.get(i);
            String line = String.format("%s %s [%s]%n",
                    numbers.get(i).getAsEmoji().getFormatted(), anime.getTitle(), anime.getType());
            sb.append(line);
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Anime Search results");
        builder.setDescription(sb + "\nTo select one item, please use the according reaction");
        builder.setColor(getEmbedColor());
        return builder;
    }

    @Override
    protected void setEmbedDetails(EmbedBuilder builder, Anime anime) {
        if (anime.getTitleEnglish() != null && !anime.getTitleEnglish().equals(anime.getTitle())) {
            builder.addField("English Title", anime.getTitleEnglish(), true);
        }
        if (anime.getTitleJapanese() != null) {
            builder.addField("Japanese Title", anime.getTitleJapanese(), true);
        }

        String studios = formatList(anime.getStudios());
        if (studios != null) {
            builder.addField("Studio", studios, false);
        }

        String genres = formatList(anime.getGenres());
        if (genres != null) {
            builder.addField("Genres", genres, false);
        }
        String themes = formatList(anime.getThemes());
        if (themes != null) {
            builder.addField("Themes", themes, false);
        }

        builder.addField("Status", anime.getStatus(), true);
        if ("Movie".equals(anime.getType())) {
            builder.addField("Season", formatAnimeSeason(anime), true);
            builder.addBlankField(true);
        } else {
            builder.addField("Episodes", formatAnimeEpisodes(anime), true);
            builder.addField("Season", formatAnimeSeason(anime), true);
        }

        builder.addField("MAL Score", formatScore(anime.getScore()), true);
        builder.addField("MAL Rank", formatRank(anime.getRank()), true);
        builder.addBlankField(true);
        builder.addField("Link", "[MyAnimeList](" + anime.getUrl() + ")", false);
    }

    private String formatAnimeEpisodes(Anime anime) {
        return anime.getEpisodes() == 0 ? "TBA" : String.valueOf(anime.getEpisodes());
    }

    private String formatAnimeSeason(Anime anime) {
        String season = anime.getSeason() == null ? "TBA" : anime.getSeason();
        return Formatter.capitalize(season);
    }
}