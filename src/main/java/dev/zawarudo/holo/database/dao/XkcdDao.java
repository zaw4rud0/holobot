package dev.zawarudo.holo.database.dao;

import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.database.SQLManager;
import dev.zawarudo.holo.modules.xkcd.XkcdComic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class XkcdDao {

    private static final int BATCH_SIZE = 100;

    private final SQLManager sql;

    public XkcdDao(SQLManager sql) {
        this.sql = sql;
    }

    public List<XkcdComic> findAll() throws SQLException {
        final String stmt = sql.getStatement("xkcd/select-all-xkcd-comics");

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(stmt);
             ResultSet rs = ps.executeQuery()) {

            List<XkcdComic> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        }
    }

    public void insert(XkcdComic comic) throws SQLException {
        if (comic == null) throw new IllegalArgumentException("comic must not be null");

        final String stmt = sql.getStatement("xkcd/insert-xkcd-comic");

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(stmt)) {

            bindInsert(ps, comic);
            ps.executeUpdate();
        }
    }

    public void insertAll(List<XkcdComic> comics) throws SQLException {
        if (comics == null) throw new IllegalArgumentException("comics must not be null");
        if (comics.isEmpty()) return;

        final String stmt = sql.getStatement("xkcd/insert-xkcd-comic");

        try (Connection conn = Database.getConnection()) {
            boolean origAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(stmt)) {
                int i = 0;

                for (XkcdComic comic : comics) {
                    if (comic == null) continue;

                    bindInsert(ps, comic);
                    ps.addBatch();

                    if (++i % BATCH_SIZE == 0) {
                        ps.executeBatch();
                    }
                }

                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(origAuto);
            }
        }
    }

    public List<XkcdComic> search(String ftsQuery, int limit, int offset) throws SQLException {
        if (ftsQuery == null || ftsQuery.isBlank()) {
            return List.of();
        }

        if (limit <= 0) {
            limit = 8;
        }
        if (offset < 0) {
            offset = 0;
        }

        final String stmt = sql.getStatement("xkcd/search-xkcd");

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(stmt)) {

            ps.setString(1, ftsQuery);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                List<XkcdComic> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
                return result;
            }
        }
    }

    public List<XkcdComic> searchPrioritized(String broadQuery, String phraseQuery, int limit, int offset) throws SQLException {
        if (broadQuery == null || broadQuery.isBlank()) return List.of();
        if (limit <= 0) limit = 8;
        if (offset < 0) offset = 0;

        final String stmt = sql.getStatement("xkcd/search-xkcd-prioritized");

        String safePhrase = (phraseQuery == null || phraseQuery.isBlank()) ? "\"__no_such_phrase__\"" : phraseQuery;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(stmt)) {

            ps.setString(1, safePhrase);
            ps.setString(2, broadQuery);
            ps.setInt(3, limit);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()) {
                List<XkcdComic> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
                return result;
            }
        }
    }

    private static void bindInsert(PreparedStatement ps, XkcdComic comic) throws SQLException {
        ps.setInt(1, comic.getIssueNr());
        ps.setString(2, comic.getTitle());
        ps.setString(3, comic.getAlt());
        ps.setString(4, comic.getImg());
        ps.setInt(5, comic.getDay());
        ps.setInt(6, comic.getMonth());
        ps.setInt(7, comic.getYear());
    }

    private static XkcdComic mapRow(ResultSet rs) throws SQLException {
        XkcdComic comic = new XkcdComic();
        comic.setIssueNr(rs.getInt("id"));
        comic.setTitle(rs.getString("title"));
        comic.setAlt(rs.getString("alt"));
        comic.setImg(rs.getString("img"));
        comic.setDate(
                rs.getInt("day"),
                rs.getInt("month"),
                rs.getInt("year")
        );
        return comic;
    }
}