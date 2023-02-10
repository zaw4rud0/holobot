package dev.zawarudo.holo.scripts;

import dev.zawarudo.holo.apis.xkcd.XkcdAPI;
import dev.zawarudo.holo.apis.xkcd.XkcdComic;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.exceptions.InvalidRequestException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Scrapes the xkcd API and stores new comics in the database.
 */
public final class XkcdScraper implements Runnable {

    private XkcdScraper() {
    }

    @Override
    public void run() {
        try {
            scrape();
        } catch (APIException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
            return;
        }

        List<XkcdComic> comics = new ArrayList<>();

        if (last >= newest) {
            return;
        }

        System.out.println("New issues found!");
        for (int i = last + 1; i <= newest; i++) {

            // Issue doesn't exist
            if (i == 404) {
                continue;
            }

            XkcdComic comic;
            try {
                comic = XkcdAPI.getComic(i);
            } catch (InvalidRequestException e) {
                System.out.println("XkcdScraper: Something went wrong! (InvalidRequestException)");
                return;
            }
            comics.add(comic);
            System.out.println("Added xkcd " + comic.getIssueNr() + ": " + comic.getTitle());
        }

        // Store to database
        try {
            DBOperations.insertXkcdComics(comics);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Stored to the database!");
    }

    public static void main(String[] args) {
        new XkcdScraper().run();
    }
}