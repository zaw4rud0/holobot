package dev.zawarudo.holo.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.holo.misc.Emote;
import dev.zawarudo.holo.utils.HoloUtils;
import dev.zawarudo.nanojikan.JikanAPI;
import dev.zawarudo.nanojikan.exception.APIException;
import dev.zawarudo.nanojikan.exception.InvalidRequestException;
import dev.zawarudo.nanojikan.model.Manga;
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

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        sendTyping(event);

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a title to search for.");
            return;
        }

        String search = String.join(" ", args);

        List<Manga> result;
        try {
            result = JikanAPI.searchManga(search);
        } catch (InvalidRequestException e) {
            sendErrorEmbed(event, "Something went wrong while searching for the manga! Please try again later.");
            logError("Invalid request: " + e.getMessage() + "! This wasn't supposed to happen!");
            return;
        } catch (APIException e) {
            sendErrorEmbed(event, "An error occurred while trying to search for the manga! Please try again later.");
            logError("An API error occurred while trying to search for the manga: " + e.getMessage() + " | Manga: " + search);
            return;
        }

        if (result.isEmpty()) {
            sendErrorEmbed(event, "I couldn't find any mangas with your given search terms!");
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
                    sendManga(event, result.get(selected.get()));
                },
                5,
                TimeUnit.MINUTES,
                () -> msg.delete().queue()
        );
    }

    private EmbedBuilder getResultsEmbed(List<Manga> result) {
        List<Emote> numbers = HoloUtils.getNumbers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            Manga manga = result.get(i);
            String line = String.format("%s %s [%s]\n", numbers.get(i).getAsEmoji().getFormatted(), manga.getTitle(), manga.getType());
            sb.append(line);
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Manga Search results");
        builder.setDescription(sb + "\nTo select one item, please use the according reaction");
        builder.setColor(getEmbedColor());
        return builder;
    }

    private void sendManga(MessageReceivedEvent event, Manga manga) {
        EmbedBuilder builder = new EmbedBuilder();

        // Prepare fields
        String genres = null;
        String themes = null;
        if (manga.getGenres() != null && manga.getGenres().size() != 0) {
            genres = manga.getGenres().toString().replace("[", "").replace("]", "");
        }
        if (manga.getThemes() != null && manga.getThemes().size() != 0) {
            themes = manga.getThemes().toString().replace("[", "").replace("]", "");
        }
        int ch = manga.getChapters();
        int vol = manga.getVolumes();
        String chapters = ch != 0 ? vol != 0 ? "Vol: " + vol + "\nCh: " + ch : ch + "Ch." : "TBA";
        String malScore = manga.getScore() != 0.0 ? String.valueOf(manga.getScore()) : "N/A";
        String malRank = manga.getRank() != 0 ? String.valueOf(manga.getRank()) : "N/A";

        // Set embed
        builder.setTitle(manga.getTitle());
        builder.setThumbnail(manga.getImages().getJpg().getLargeImage());
        if (manga.hasSynopsis()) {
            builder.setDescription(manga.getSynopsis());
        }
        if (manga.getTitleEnglish() != null && !manga.getTitleEnglish().equals(manga.getTitle())) {
            builder.addField("English Title", manga.getTitleEnglish(), true);
        }
        if (manga.getTitleJapanese() != null) {
            builder.addField("Japanese Title", manga.getTitleJapanese(), true);
        }
        if (genres != null) {
            builder.addField("Genres", genres, false);
        }
        if (themes != null) {
            builder.addField("Themes", themes, false);
        }
        builder.addField("Type", manga.getType(), true);
        builder.addField("Chapters", chapters, true);
        builder.addField("Status", manga.getStatus(), true);
        builder.addField("MAL Score", malScore, true);
        builder.addField("MAL Rank", malRank, true);
        builder.addBlankField(true);
        builder.addField("Link", "[MyAnimeList](" + manga.getUrl() + ")", false);
        sendEmbed(event, builder, true, getEmbedColor());
    }
}