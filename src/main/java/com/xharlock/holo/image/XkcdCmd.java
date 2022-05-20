package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.database.DBOperations;
import com.xharlock.holo.exceptions.APIException;
import com.xharlock.holo.exceptions.InvalidRequestException;
import com.xharlock.holo.misc.EmbedColor;

import dev.zawarudo.apis.xkcd.XkcdAPI;

import dev.zawarudo.apis.xkcd.XkcdComic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Command(name = "xkcd",
        description = "Use this command to access the comics of xkcd.",
        usage = "[new | <issue nr> | <title>]",
        thumbnail = "https://xkcd.com/s/0b7742.png",
        embedColor = EmbedColor.WHITE,
        category = CommandCategory.IMAGE)
public class XkcdCmd extends AbstractCommand {

    /* TODO: Retrieve issues from the DB instead of storing them in a map */
    private final Map<Integer, XkcdComic> comics;
    private int newestIssue;

    public XkcdCmd() {
        comics = new HashMap<>();

        try {
            List<XkcdComic> list = DBOperations.getXkcdComics();
            Collections.sort(list);

            // Get the newest issue
            newestIssue = list.get(list.size() - 1).getIssueNr();

            for (XkcdComic comic : list) {
                comics.put(comic.getIssueNr(), comic);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCommand(MessageReceivedEvent e) {
        deleteInvoke(e);
        EmbedBuilder builder = new EmbedBuilder();
        XkcdComic comic = null;

        // Random issue
        if (args.length == 0) {
            try {
                comic = XkcdAPI.getComic(new Random().nextInt(newestIssue) + 1);
                storeIfNew(comic);
            } catch (APIException | InvalidRequestException | SQLException ex) {
                sendErrorMessage(e, "Something went wrong while retrieving the comic. Please try again later.");
                return;
            }
        }

        // Newest issue
        else if (args[0].equals("new")) {
            try {
                comic = XkcdAPI.getLatest();
                storeIfNew(comic);
            } catch (APIException | SQLException ex) {
                sendErrorMessage(e, "Something went wrong while retrieving the comic. Please try again later.");
                return;
            }
        }

        // Specific issue by number
        else if (isInteger(args[0])) {
            int num = Integer.parseInt(args[0]);

            if (num > newestIssue || num < 1) {
                sendErrorMessage(e, "This comic does not exist! If you think it should exist, consider using `" + getPrefix(e) + "xkcd new` to refresh ny database.");
                return;
            }

            try {
                comic = XkcdAPI.getComic(num);
            } catch (APIException | InvalidRequestException ex) {
                sendErrorMessage(e, "Something went wrong while retrieving the comic. Please try again later.");
                return;
            }
        }

        // Specific issue by title
        else {
            String title = String.join(" ", args).toLowerCase(Locale.UK);

            for (XkcdComic c : comics.values()) {
                if (c.getTitle().toLowerCase(Locale.UK).equals(title)) {
                    comic = c;
                    break;
                }
            }

            // Couldn't find the comic
            if (comic == null) {
                sendErrorMessage(e, "This comic does not exist! If you think it should exist, consider using `" + getPrefix(e) + "xkcd new` to refresh ny database.");
                return;
            }
        }

        // Builds the embed and sends it
        builder.setTitle("xkcd " + comic.getIssueNr() + ": " + comic.getTitle());
        builder.setDescription(comic.getAlt() + "\n\n[Explanation](" + comic.getExplainedUrl() + ")");
        builder.setImage(comic.getImg());
        sendEmbed(e, builder, true, getEmbedColor());
    }

    private void sendErrorMessage(MessageReceivedEvent e, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Error");
        builder.setDescription(message);
        sendEmbed(e, builder, 30, TimeUnit.SECONDS, true, Color.RED);
    }

    private void storeIfNew(XkcdComic comic) throws SQLException {
        int num = comic.getIssueNr();

        // Not new
        if (num <= newestIssue) {
            return;
        }

        List<XkcdComic> newComics = new ArrayList<>();

        // Store all comics up to the newest issue
        for (int i = newestIssue + 1; i <= num; i++) {
            XkcdComic newComic;
            try {
                newComic = XkcdAPI.getComic(i);
            } catch (APIException | InvalidRequestException ex) {
                ex.printStackTrace();
                return;
            }
            comics.put(newComic.getIssueNr(), newComic);
            newComics.add(newComic);
        }
        newestIssue = num;

        // Store new comics in the database
        DBOperations.insertXkcdComics(newComics);
    }
}