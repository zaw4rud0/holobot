package dev.zawarudo.holo.commands.fun;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.database.dao.XkcdDao;
import dev.zawarudo.holo.modules.xkcd.XkcdAPI;
import dev.zawarudo.holo.modules.xkcd.XkcdComic;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Command(name = "xkcd",
        description = "Use this command to access the comics of xkcd.",
        usage = "[new | search <query> | <issue nr> | <title>]",
        thumbnail = "https://xkcd.com/s/0b7742.png",
        embedColor = EmbedColor.WHITE,
        category = CommandCategory.MISC)
public class XkcdCmd extends AbstractCommand {

    private static final Random RANDOM = new Random();

    private static final String ERROR_RETRIEVING = "Something went wrong while retrieving the comic. Please try again later.";
    private static final String ERROR_DOES_NOT_EXIST = "This comic does not exist! If you think it should exist, consider using `%sxkcd new` to refresh my database.";

    private static final int SEARCH_LIMIT = 8;

    private final XkcdDao xkcdDao;

    private final Map<Integer, XkcdComic> comics = new ConcurrentHashMap<>();

    private final AtomicInteger latestIssue = new AtomicInteger();
    private final AtomicInteger maxStoredIssue = new AtomicInteger();

    public XkcdCmd(XkcdDao xkcdDao) {
        this.xkcdDao = xkcdDao;

        loadComicsFromDb();

        // Try to fetch latest once
        try {
            ensureLatestIssue();
        } catch (APIException ex) {
            logger.warn("Failed to fetch latest XKCD issue during startup. Will retry on demand.", ex);
        }
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 0) {
            sendRandomComic(event);
            return;
        }

        // Fetch and display newest xkcd comic
        if (args.length == 1 && args[0].equalsIgnoreCase("new")) {
            sendNewestComic(event);
            return;
        }

        // Full-text search using FTS5
        if (args.length >= 2 && args[0].equalsIgnoreCase("search")) {
            sendSearch(event);
            return;
        }

        // Fetch comic by issue number
        if (isInteger(args[0])) {
            sendComicByIssueNumber(event);
            return;
        }

        // Fetch comic by title
        sendComicByTitle(event);
    }

    private void sendRandomComic(MessageReceivedEvent event) {
        try {
            int upper = latestIssue.get();
            if (upper <= 0) upper = maxStoredIssue.get();

            if (upper <= 0) {
                ensureLatestIssue();
                upper = latestIssue.get();
            }

            if (upper <= 0) {
                sendErrorEmbed(event, ERROR_RETRIEVING);
                return;
            }

            int issue = RANDOM.nextInt(upper) + 1;

            // 404 intentionally missing
            if (issue == 404) {
                issue = (upper == 1) ? 1 : RANDOM.nextInt(upper) + 1;
                if (issue == 404) issue = 403;
            }

            XkcdComic comic = XkcdAPI.getComic(issue);
            storeComic(comic);
            sendXkcd(event, comic);
        } catch (APIException | InvalidRequestException | SQLException ex) {
            logger.error("Failed to fetch/store random XKCD comic.", ex);
            sendErrorEmbed(event, ERROR_RETRIEVING);
        }
    }

    private void sendNewestComic(MessageReceivedEvent event) {
        try {
            XkcdComic comic = XkcdAPI.getLatest();

            latestIssue.updateAndGet(cur -> Math.max(cur, comic.getIssueNr()));

            storeComic(comic);
            sendXkcd(event, comic);
        } catch (APIException | SQLException ex) {
            logger.error("Failed to fetch/store newest XKCD comic.", ex);
            sendErrorEmbed(event, ERROR_RETRIEVING);
        }
    }

    private void sendSearch(MessageReceivedEvent event) {
        String raw = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
        if (raw.isBlank()) {
            sendErrorEmbed(event, "Usage: `" + getPrefix(event) + "xkcd search <query>`");
            return;
        }

        String broadQuery = toBroadQuery(raw);
        if (broadQuery.isBlank()) {
            sendErrorEmbed(event, "Search query is empty after filtering.");
            return;
        }

        String phraseQuery = toPhraseQuery(raw);

        try {
            List<XkcdComic> results = xkcdDao.searchPrioritized(broadQuery, phraseQuery, SEARCH_LIMIT, 0);

            if (results.isEmpty()) {
                event.getMessage().replyEmbeds(
                        new EmbedBuilder()
                                .setTitle("xkcd Search Results")
                                .setDescription("No results found for:\n`" + raw + "`")
                                .setColor(getEmbedColor())
                                .build()
                ).queue();
                return;
            }

            StringBuilder body = new StringBuilder();
            for (XkcdComic comic : results) {
                body.append(comic.getIssueNr()).append('\n')
                        .append(comic.getTitle()).append('\n')
                        .append(comic.getAlt()).append("\n\n");
            }

            event.getMessage().replyEmbeds(
                    new EmbedBuilder()
                            .setTitle("xkcd Search Results")
                            .setDescription(Formatter.asCodeBlock(body.toString()))
                            .setFooter("Showing top " + results.size()
                                    + " results • Open with " + getPrefix(event) + "xkcd <issue nr>")
                            .setColor(getEmbedColor())
                            .build()
            ).queue();

        } catch (SQLException ex) {
            logger.error("XKCD search failed. broadQuery='{}' phraseQuery='{}'", broadQuery, phraseQuery, ex);
            sendErrorEmbed(event, ERROR_RETRIEVING);
        }
    }

    private void sendComicByIssueNumber(MessageReceivedEvent event) {
        int num = Integer.parseInt(args[0]);
        if (num < 1) {
            sendErrorEmbed(event, String.format(ERROR_DOES_NOT_EXIST, getPrefix(event)));
            return;
        }

        try {
            if (latestIssue.get() <= 0) {
                ensureLatestIssue();
            }

            int latest = latestIssue.get();
            if (latest > 0 && num > latest) {
                sendErrorEmbed(event, String.format(ERROR_DOES_NOT_EXIST, getPrefix(event)));
                return;
            }

            XkcdComic comic = XkcdAPI.getComic(num);
            storeComic(comic);
            sendXkcd(event, comic);
        } catch (APIException | InvalidRequestException | SQLException ex) {
            logger.error("Failed to fetch/store XKCD comic #{}.", num, ex);
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
        alt = alt.length() > MessageEmbed.TEXT_MAX_LENGTH
                ? alt.substring(0, MessageEmbed.TEXT_MAX_LENGTH - 3) + "..."
                : alt;

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

    private void storeComic(XkcdComic comic) throws SQLException {
        comics.put(comic.getIssueNr(), comic);

        maxStoredIssue.updateAndGet(cur -> Math.max(cur, comic.getIssueNr()));
        latestIssue.updateAndGet(cur -> Math.max(cur, comic.getIssueNr()));

        xkcdDao.insert(comic);
    }

    private void ensureLatestIssue() throws APIException {
        if (latestIssue.get() > 0) return;

        XkcdComic latest = XkcdAPI.getLatest();

        latestIssue.updateAndGet(cur -> Math.max(cur, latest.getIssueNr()));

        try {
            storeComic(latest);
        } catch (SQLException ex) {
            logger.error("Failed to store latest XKCD comic during ensureLatestIssue().", ex);
        }
    }

    private static String toBroadQuery(String raw) {
        String[] parts = raw.toLowerCase().trim().split("\\s+");
        List<String> tokens = new ArrayList<>();

        for (String p : parts) {
            String t = p.replaceAll("[^\\p{L}\\p{N}]+", "");
            if (!t.isBlank()) tokens.add(t);
        }

        if (tokens.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) sb.append(" AND ");
            sb.append('"').append(tokens.get(i)).append('"');
            if (i == tokens.size() - 1) sb.append('*');
        }
        return sb.toString();
    }

    private static String toPhraseQuery(String raw) {
        String normalized = raw.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ") // Replace extra spaces
                .replace("\"", "");

        if (normalized.isBlank()) return "";

        return "\"" + normalized + "\"";
    }

    private void loadComicsFromDb() {
        try {
            List<XkcdComic> list = xkcdDao.findAll();

            if (list.isEmpty()) {
                logger.warn("No XKCD comics found in DB.");
                return;
            }

            Collections.sort(list);
            logger.info("Loaded {} XKCD comics from DB.", list.size());

            int max = list.getLast().getIssueNr();
            maxStoredIssue.set(max);

            for (XkcdComic comic : list) {
                comics.put(comic.getIssueNr(), comic);
            }
        } catch (SQLException ex) {
            logger.error("Failed to load XKCD comics from DB.", ex);
        }
    }
}