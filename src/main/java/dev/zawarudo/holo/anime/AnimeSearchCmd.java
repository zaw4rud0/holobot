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
        List<Anime> result = performAnimeSearch(event, search);

        if (result.isEmpty()) {
            sendErrorEmbed(event, "I couldn't find any animes with your given search terms!");
            return;
        }

        deleteInvoke(event);

        showSearchResults(event, result);
    }

    private List<Anime> performAnimeSearch(MessageReceivedEvent event, String search) {
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

    private EmbedBuilder createSearchResultEmbed(List<Anime> result) {
        List<Emote> numbers = HoloUtils.getNumbers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            Anime anime = result.get(i);
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

    private void showSearchResults(MessageReceivedEvent event, List<Anime> result) {
        EmbedBuilder builder = createSearchResultEmbed(result);
        Message msg = event.getChannel().sendMessageEmbeds(builder.build()).complete();
        User caller = event.getAuthor();

        HoloUtils.addReactions(msg, result.size());
        AtomicInteger selected = new AtomicInteger(-1);

        waitForUserReaction(event, msg, caller, result, selected);
    }

    private void waitForUserReaction(MessageReceivedEvent event, Message msg, User caller, List<Anime> result, AtomicInteger selected) {
        waiter.waitForEvent(
                MessageReactionAddEvent.class,
                evt -> isReactionValid(evt, msg, caller, result, selected),
                evt -> handleUserReaction(event, msg, result, selected),
                5,
                TimeUnit.MINUTES,
                () -> msg.delete().queue()
        );
    }

    private boolean isReactionValid(MessageReactionAddEvent evt, Message msg, User caller, List<Anime> result, AtomicInteger selected) {
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

    private void handleUserReaction(MessageReceivedEvent event, Message msg, List<Anime> result, AtomicInteger selected) {
        msg.delete().queue();
        sendAnime(event, result.get(selected.get()));
    }

    private void sendAnime(MessageReceivedEvent event, Anime anime) {
        EmbedBuilder builder = createEmbedBuilder(anime);
        setAnimeDetails(builder, anime);
        sendEmbed(event, builder, true, getEmbedColor());
    }

    private EmbedBuilder createEmbedBuilder(Anime anime) {
        EmbedBuilder builder = new EmbedBuilder();
        String type = anime.getType() == null ? "null" : anime.getType();

        String title = Formatter.truncateString(anime.getTitle(), MessageEmbed.TITLE_MAX_LENGTH - (type.length() + 3));
        builder.setTitle(String.format("%s [%s]", title, type));

        builder.setThumbnail(anime.getImages().getJpg().getLargeImage());
        if (anime.hasSynopsis()) {
            String synopsis = Formatter.truncateString(anime.getSynopsis(), MessageEmbed.DESCRIPTION_MAX_LENGTH);
            builder.setDescription(synopsis);
        }
        return builder;
    }

    private void setAnimeDetails(EmbedBuilder builder, Anime anime) {
        if (anime.getTitleEnglish() != null && !anime.getTitleEnglish().equals(anime.getTitle())) {
            builder.addField("English Title", anime.getTitleEnglish(), true);
        }
        if (anime.getTitleJapanese() != null) {
            builder.addField("Japanese Title", anime.getTitleJapanese(), true);
        }

        String studios = getFormattedList(anime.getStudios());
        if (studios != null) {
            builder.addField("Studio", studios, false);
        }

        String genres = getFormattedList(anime.getGenres());
        if (genres != null) {
            builder.addField("Genres", genres, false);
        }
        String themes = getFormattedList(anime.getThemes());
        if (themes != null) {
            builder.addField("Themes", themes, false);
        }

        builder.addField("Status", anime.getStatus(), true);
        if ("Movie".equals(anime.getType())) {
            builder.addField("Season", getAnimeSeason(anime), true);
            builder.addBlankField(true);
        } else {
            builder.addField("Episodes", getAnimeEpisodes(anime), true);
            builder.addField("Season", getAnimeSeason(anime), true);
        }

        builder.addField("MAL Score", getFormattedScore(anime.getScore()), true);
        builder.addField("MAL Rank", getFormattedRank(anime.getRank()), true);
        builder.addBlankField(true);
        builder.addField("Link", "[MyAnimeList](" + anime.getUrl() + ")", false);
    }

    private String getFormattedList(List<Nameable> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<String> strings = list.stream().map(Nameable::toString).toList();
        return String.join(", ", strings);
    }

    private String getAnimeEpisodes(Anime anime) {
        return anime.getEpisodes() == 0 ? "TBA" : String.valueOf(anime.getEpisodes());
    }

    private String getAnimeSeason(Anime anime) {
        String season = anime.getSeason() == null ? "TBA" : anime.getSeason();
        return Formatter.capitalize(season);
    }

    private String getFormattedScore(double score) {
        return score == 0.0 ? "N/A" : String.valueOf(score);
    }

    private String getFormattedRank(int rank) {
        return rank == 0 ? "N/A" : String.valueOf(rank);
    }
}