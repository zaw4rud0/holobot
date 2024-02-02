package dev.zawarudo.holo.scripts;

import dev.zawarudo.holo.modules.xkcd.XkcdAPI;
import dev.zawarudo.holo.modules.xkcd.XkcdComic;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private XkcdScraper() {
    }

    public static void scrape() throws APIException {
        int newest = XkcdAPI.getLatest().getIssueNr();
        int last = 0;
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM XkcdComics ORDER BY id DESC Limit 1;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                last = rs.getInt("id");
            }
            ps.close();
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Something went wrong while reading the XKCD comics from the DB.");
            }
            return;
        }

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

            XkcdComic comic;
            try {
                comic = XkcdAPI.getComic(i);
            } catch (InvalidRequestException e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Something went wrong!", e);
                }
                return;
            }
            comics.add(comic);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Added xkcd {}: {}", comic.getIssueNr(), comic.getTitle());
            }
        }

        // Store to database
        try {
            DBOperations.insertXkcdComics(comics);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Something went wrong!", e);
            }
            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Stored to the database!");
        }
    }
}