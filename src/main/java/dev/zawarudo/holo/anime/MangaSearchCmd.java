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
import dev.zawarudo.nanojikan.model.Manga;
import dev.zawarudo.nanojikan.model.Nameable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
public class MangaSearchCmd extends AbstractCommand {

    private final EventWaiter waiter;
    private final List<Emote> selection = HoloUtils.getNumbers();

    public MangaSearchCmd(EventWaiter waiter) {
        this.waiter = waiter;
    }

    public void onCommand(@NotNull MessageReceivedEvent event) {
        sendTyping(event);

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a title to search for.");
            return;
        }

        String search = String.join(" ", args);
        List<Manga> result = performMangaSearch(event, search);

        if (result.isEmpty()) {
            sendErrorEmbed(event, "I couldn't find any mangas with your given search terms!");
            return;
        }

        deleteInvoke(event);

        showSearchResults(event, result);
    }

    private List<Manga> performMangaSearch(MessageReceivedEvent event, String search) {
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

    private EmbedBuilder createSearchResultEmbed(List<Manga> result) {
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

    private void showSearchResults(MessageReceivedEvent event, List<Manga> result) {
        EmbedBuilder builder = createSearchResultEmbed(result);
        Message msg = event.getChannel().sendMessageEmbeds(builder.build()).complete();
        User caller = event.getAuthor();

        HoloUtils.addReactions(msg, result.size());
        AtomicInteger selected = new AtomicInteger(-1);

        waitForUserReaction(event, msg, caller, result, selected);
    }

    private void waitForUserReaction(MessageReceivedEvent event, Message msg, User caller, List<Manga> result, AtomicInteger selected) {
        waiter.waitForEvent(
                MessageReactionAddEvent.class,
                evt -> isReactionValid(evt, msg, caller, result, selected),
                evt -> handleUserReaction(event, msg, result, selected),
                5,
                TimeUnit.MINUTES,
                () -> msg.delete().queue()
        );
    }

    private boolean isReactionValid(MessageReactionAddEvent evt, Message msg, User caller, List<Manga> result, AtomicInteger selected) {
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
    }

    private void handleUserReaction(MessageReceivedEvent event, Message msg, List<Manga> result, AtomicInteger selected) {
        msg.delete().queue();
        sendManga(event, result.get(selected.get()));
    }

    private void sendManga(MessageReceivedEvent event, Manga manga) {
        EmbedBuilder builder = createEmbedBuilder(manga);
        setMangaDetails(builder, manga);
        sendEmbed(event, builder, true, getEmbedColor());
    }

    private EmbedBuilder createEmbedBuilder(Manga manga) {
        EmbedBuilder builder = new EmbedBuilder();
        String type = manga.getType() == null ? "null" : manga.getType();

        String title = Formatter.truncateString(manga.getTitle(), MessageEmbed.TITLE_MAX_LENGTH - (type.length() + 3));
        builder.setTitle(String.format("%s [%s]", title, type));

        builder.setThumbnail(manga.getImages().getJpg().getLargeImage());
        if (manga.hasSynopsis()) {
            String synopsis = Formatter.truncateString(manga.getSynopsis(), MessageEmbed.DESCRIPTION_MAX_LENGTH);
            builder.setDescription(synopsis);
        }
        return builder;
    }

    private void setMangaDetails(EmbedBuilder builder, Manga manga) {
        if (manga.getTitleEnglish() != null && !manga.getTitleEnglish().equals(manga.getTitle())) {
            builder.addField("English Title", manga.getTitleEnglish(), true);
        }
        if (manga.getTitleJapanese() != null) {
            builder.addField("Japanese Title", manga.getTitleJapanese(), true);
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

    private String formatList(List<Nameable> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<String> strings = list.stream().map(Nameable::toString).toList();
        return String.join(", ", strings);
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

    private String formatScore(double score) {
        return score == 0.0 ? "N/A" : String.valueOf(score);
    }

    private String formatRank(int rank) {
        return rank == 0 ? "N/A" : String.valueOf(rank);
    }
}