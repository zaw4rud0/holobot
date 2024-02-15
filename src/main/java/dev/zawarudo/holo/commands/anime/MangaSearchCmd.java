package dev.zawarudo.holo.commands.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.modules.jikan.JikanAPI;
import dev.zawarudo.holo.modules.jikan.model.Manga;
import dev.zawarudo.holo.modules.jikan.model.Nameable;
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
import java.util.stream.Collectors;

/**
 * A command for searching and displaying manga information from the MyAnimeList
 * database. It uses JikanAPI since the official MyAnimeList API doesn't provide
 * all needed functionalities. See {@link JikanAPI} for more info.
 */
@Command(name = "mangasearch",
        description = "Use this command to search for a manga in the database of MyAnimeList.",
        usage = "<title>",
        example = "black clover",
        alias = {"ms", "manga"},
        thumbnail = "https://upload.wikimedia.org/wikipedia/commons/7/7a/MyAnimeList_Logo.png",
        embedColor = EmbedColor.MAL,
        category = CommandCategory.ANIME)
public class MangaSearchCmd extends BaseSearchCmd<Manga> {

    public MangaSearchCmd(EventWaiter waiter) {
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
        List<Manga> result = performSearch(event, search);

        if (result.isEmpty()) {
            sendErrorEmbed(event, "I couldn't find any mangas with your given search terms!");
            return;
        }

        deleteInvoke(event);
        showSearchResults(event, result);
    }

    @Override
    protected List<Manga> performSearch(MessageReceivedEvent event, String search) {
        try {
            return JikanAPI.searchManga(search);
        } catch (InvalidRequestException ex) {
            sendErrorEmbed(event, "An error occurred while trying to search for the manga! Please try again later.");
            if (logger.isErrorEnabled()) {
                logger.error("Invalid request! This wasn't supposed to happen!", ex);
            }
        } catch (APIException ex) {
            sendErrorEmbed(event, "An error occurred while trying to search for the manga! Please try again later.");
            if (logger.isErrorEnabled()) {
                logger.error("An API error occurred while trying to search for the anime. Manga: " + search, ex);
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected EmbedBuilder createSearchResultEmbed(List<Manga> result) {
        List<Emote> numbers = HoloUtils.getNumbers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            Manga manga = result.get(i);
            String line = String.format("%s %s [%s]%n",
                    numbers.get(i).getAsEmoji().getFormatted(), manga.getTitle(), manga.getType());
            sb.append(line);
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Manga Search results");
        builder.setDescription(sb + "\nTo select one item, please use the according reaction");
        builder.setColor(getEmbedColor());
        return builder;
    }

    @Override
    protected void setEmbedDetails(EmbedBuilder builder, Manga manga) {
        if (manga.getTitleEnglish().isPresent() && !manga.getTitleEnglish().equals(manga.getTitle())) {
            builder.addField("English Title", manga.getTitleEnglish().get(), true);
        }
        if (manga.getTitleJapanese().isPresent()) {
            builder.addField("Japanese Title", manga.getTitleJapanese().get(), true);
        }

        String authors = formatAuthors(manga.getAuthors());
        if (authors != null) {
            builder.addField("Author/Mangaka", authors, false);
        }

        String genres = formatList(manga.getGenres());
        if (genres != null) {
            builder.addField("Genres", genres, false);
        }
        String themes = formatList(manga.getThemes());
        if (themes != null) {
            builder.addField("Themes", themes, false);
        }
        String demographics = formatList(manga.getDemographics());
        if (demographics != null) {
            builder.addField("Demographics", demographics, false);
        }

        builder.addField("Status", manga.getStatus(), true);
        int chapters = manga.getChapters();
        int volumes = manga.getVolumes();

        if (manga.getType().equals("Light Novel")) {
            builder.addField("Volumes", formatChapters(chapters, volumes), true);
        } else {
            builder.addField("Chapters", formatChapters(chapters, volumes), true);
        }

        builder.addBlankField(true);

        builder.addField("MAL Score", formatScore(manga.getScore()), true);
        builder.addField("MAL Rank", formatRank(manga.getRank()), true);
        builder.addBlankField(true);
        builder.addField("Link", "[MyAnimeList](" + manga.getUrl() + ")", false);
    }

    private String formatAuthors(List<Nameable> list) {
        return list.stream().map(Nameable::getName).map(Formatter::reverseJapaneseName).collect(Collectors.joining(", "));
    }

    private String formatChapters(int ch, int vol) {
        String displayText;
        if (ch != 0) {
            if (vol != 0) {
                displayText = String.format("Vol: %d%nCh: %d", vol, ch);
            } else {
                displayText = String.format("%d Ch.", ch);
            }
        } else {
            displayText = "TBA";
        }
        return displayText;
    }
}