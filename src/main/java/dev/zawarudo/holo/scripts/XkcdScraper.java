package dev.zawarudo.holo.scripts;

import dev.zawarudo.holo.database.SQLManager;
import dev.zawarudo.holo.modules.xkcd.XkcdAPI;
import dev.zawarudo.holo.modules.xkcd.XkcdComic;
import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Scrapes the xkcd API and stores new comics in the database.
 */
public final class XkcdScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(XkcdScraper.class);

    private static SQLManager sqlManager;

    private XkcdScraper() {
    }

    public static void scrape() throws APIException, IOException {
        sqlManager = new SQLManager();

        int newest = XkcdAPI.getLatest().getIssueNr();
        int last = getLatestComicFromDatabase();

        if (last >= newest) {
            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("New xkcd issues found!");
        }

        List<XkcdComic> comics = new ArrayList<>();

        for (int i = last + 1; i <= newest; i++) {
            // Issue doesn't exist
            if (i == 404) {
                continue;
            }

            XkcdComic comic = fetchXkcdComic(i);
            if (comic != null) {
                comics.add(comic);
                logComicInfo(comic);
            }
        }

        storeComicsInDatabase(comics);
    }

    private static int getLatestComicFromDatabase() {
        int last = 0;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM XkcdComics ORDER BY id DESC Limit 1;");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                last = rs.getInt("id");
            }
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Something went wrong while reading the XKCD comics from the DB.", e);
            }
        }
        return last;
    }

    private static XkcdComic fetchXkcdComic(int comicId) throws APIException {
        try {
            return XkcdAPI.getComic(comicId);
        } catch (InvalidRequestException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Something went wrong while fetching xkcd comic: " + comicId, e);
            }
        }
        return null;
    }

    private static void logComicInfo(XkcdComic comic) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Added xkcd {}: {}", comic.getIssueNr(), comic.getTitle());
        }
    }

    private static void storeComicsInDatabase(List<XkcdComic> comics) {
        try {
            insertXkcdComics(comics);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Stored comics in the database!");
            }
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Something went wrong while storing comics in the database.", e);
            }
        }
    }

    private static void insertXkcdComics(List<XkcdComic> comics) throws SQLException {
        String sql = sqlManager.getStatement("insert-xkcd-comic");

        Connection conn = Database.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (XkcdComic comic : comics) {
                ps.setInt(1, comic.getIssueNr());
                ps.setString(2, comic.getTitle());
                ps.setString(3, comic.getAlt());
                ps.setString(4, comic.getImg());
                ps.setInt(5, comic.getDay());
                ps.setInt(6, comic.getMonth());
                ps.setInt(7, comic.getYear());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        }

        conn.setAutoCommit(true);
        conn.close();
    }

    public static void main(String[] args) throws APIException, IOException {
        XkcdScraper.scrape();
    }
}