package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.database.dao.XkcdDao;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.modules.xkcd.XkcdAPI;
import dev.zawarudo.holo.modules.xkcd.XkcdComic;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import dev.zawarudo.holo.core.misc.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Command(name = "xkcd",
        description = "Use this command to access the comics of xkcd.",
        usage = "[new | <issue nr> | <title>]",
        thumbnail = "https://xkcd.com/s/0b7742.png",
        embedColor = EmbedColor.WHITE,
        category = CommandCategory.IMAGE)
public class XkcdCmd extends AbstractCommand {

    private static final Random RANDOM = new Random();

    private static final String ERROR_RETRIEVING = "Something went wrong while retrieving the comic. Please try again later.";
    private static final String ERROR_DOES_NOT_EXIST = "This comic does not exist! If you think it should exist, consider using `%sxkcd new` to refresh my database.";

    private final XkcdDao xkcdDao;

    private final Map<Integer, XkcdComic> comics = new HashMap<>();
    private int newestIssue;

    public XkcdCmd(XkcdDao xkcdDao) {
        this.xkcdDao = xkcdDao;

        try {
            List<XkcdComic> list = xkcdDao.findAll();

            if (list.isEmpty()) {
                logger.warn("No XKCD comics found in DB.");
                this.newestIssue = 0;
                return;
            }

            Collections.sort(list);

            logger.info("Loaded {} XKCD comics from DB.", list.size());
            this.newestIssue = list.getLast().getIssueNr();

            for (XkcdComic comic : list) {
                comics.put(comic.getIssueNr(), comic);
            }
        } catch (SQLException ex) {
            logger.error("Something went wrong while fetching the XKCD comics from the DB.", ex);
            newestIssue = 0;
        }
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 0) {
            sendRandomComic(event);
        } else if (args.length == 1 && args[0].equals("new")) {
            sendNewestComic(event);
        } else if (isInteger(args[0])) {
            sendComicByIssueNumber(event);
        } else {
            sendComicByTitle(event);
        }
    }

    private void sendRandomComic(MessageReceivedEvent event) {
        try {
            XkcdComic comic = XkcdAPI.getComic(RANDOM.nextInt(newestIssue) + 1);
            storeComicIfNew(comic);
            sendXkcd(event, comic);
        } catch (APIException | InvalidRequestException | SQLException ex) {
            sendErrorEmbed(event, ERROR_RETRIEVING);
        }
    }

    private void sendNewestComic(MessageReceivedEvent event) {
        try {
            XkcdComic comic = XkcdAPI.getLatest();
            storeComicIfNew(comic);
            sendXkcd(event, comic);
        } catch (APIException | SQLException ex) {
            sendErrorEmbed(event, ERROR_RETRIEVING);
        }
    }

    private void sendComicByIssueNumber(MessageReceivedEvent event) {
        int num = Integer.parseInt(args[0]);
        if (num > newestIssue || num < 1) {
            sendErrorEmbed(event, String.format(ERROR_DOES_NOT_EXIST, getPrefix(event)));
            return;
        }

        try {
            XkcdComic comic = XkcdAPI.getComic(num);
            sendXkcd(event, comic);
        } catch (APIException | InvalidRequestException ex) {
            sendErrorEmbed(event, ERROR_RETRIEVING);
        }
    }

    private void sendComicByTitle(MessageReceivedEvent event) {
        String title = String.join(" ", args);

        Optional<XkcdComic> optionalComic = comics.values().stream()
                .filter(c -> c.getTitle().equalsIgnoreCase(title))
                .findFirst();

        if (optionalComic.isEmpty()) {
            sendErrorEmbed(event, String.format(ERROR_DOES_NOT_EXIST, getPrefix(event)));
            return;
        }
        sendXkcd(event, optionalComic.get());
    }

    private void sendXkcd(MessageReceivedEvent event, XkcdComic comic) {
        String alt = comic.getAlt();
        alt = alt.length() > MessageEmbed.TEXT_MAX_LENGTH ? alt.substring(0, MessageEmbed.TEXT_MAX_LENGTH - 3) + "..." : alt;

        event.getMessage().replyEmbeds(
                new EmbedBuilder()
                        .setTitle("xkcd " + comic.getIssueNr() + ": " + comic.getTitle())
                        .setDescription("[Explanation](" + comic.getExplainedUrl() + ")")
                        .setImage(comic.getImg())
                        .setFooter(alt)
                        .setColor(getEmbedColor())
                        .build()
        ).queue();
    }

    private void storeComicIfNew(XkcdComic comic) throws SQLException {
        int num = comic.getIssueNr();

        // Not new
        if (num <= newestIssue) {
            return;
        }

        List<XkcdComic> newComics = new ArrayList<>();

        // Store all comics up to the newest issue
        for (int i = newestIssue + 1; i <= num; i++) {
            // 404 is intentionally missing
            if (i == 404) {
                continue;
            }

            XkcdComic newComic;
            try {
                newComic = XkcdAPI.getComic(i);
                if (logger.isInfoEnabled()) {
                    logger.info("Storing comic: {} (#{})", newComic.getTitle(), newComic.getIssueNr());
                }
            } catch (APIException | InvalidRequestException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something went wrong while storing the XKCD comics in the DB.", ex);
                }
                return;
            }
            comics.put(newComic.getIssueNr(), newComic);
            newComics.add(newComic);
        }

        newestIssue = num;
        xkcdDao.insertAll(newComics);
    }
}