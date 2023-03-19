package dev.zawarudo.holo.database;

import dev.zawarudo.holo.apis.xkcd.XkcdComic;
import dev.zawarudo.holo.misc.Submission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles all the operations on the database. Note that the connection to the database is automatically established.
 */
public final class DBOperations {

    private DBOperations() {
    }

    private static final String INSERT_EMOTE_SQL = "INSERT INTO Emotes (emote_id, emote_name, is_animated, created_at, image_url) VALUES (?, ?, ?, ?, ?);";
    private static final String INSERT_USER_SQL = "INSERT INTO DiscordUsers (user_id, username, discriminator, is_bot) VALUES (?, ?, ?, ?);";
    private static final String UPDATE_USER_SQL = "UPDATE DiscordUsers SET username = ?, discriminator = ?, is_bot = ? WHERE user_id = ?;";
    private static final String SELECT_USER_SQL = "SELECT * FROM DiscordUsers WHERE user_id = ?;";
    private static final String INSERT_GUILD_SQL = "INSERT INTO DiscordGuilds (guild_id, guild_name, owner_id) VALUES (?, ?, ?);";
    private static final String UPDATE_GUILD_SQL = "UPDATE DiscordGuilds SET guild_name = ?, owner_id = ? WHERE guild_id = ?;";
    private static final String SELECT_GUILD_SQL = "SELECT * FROM DiscordGuilds WHERE guild_id = ?;";
    private static final String INSERT_MEMBER_SQL = "INSERT INTO DiscordGuildUsers (guild_id, user_id) VALUES (?, ?);";
    private static final String DELETE_MEMBER_SQL = "DELETE FROM DiscordGuildUsers WHERE guild_id = ? AND user_id = ?;";
    private static final String DELETE_DUPLICATE_MEMBERS_SQL = "DELETE FROM DiscordGuildUsers WHERE ROWID NOT IN (SELECT min(ROWID) FROM DiscordGuildUsers GROUP BY guild_id, user_id);";
    private static final String INSERT_NICKNAME_SQL = "INSERT INTO DiscordNicknames (nickname, user_id, guild_id) VALUES (?, ?, ?);";
    private static final String INSERT_BLOCKED_IMAGE_SQL = "INSERT INTO BlockedImages (url, discord_user, date, reason) VALUES (?, ?, ?, ?);";
    private static final String SELECT_BLOCKED_IMAGE_SQL = "SELECT * FROM BlockedImages;";
    private static final String INSERT_XKCD_COMICS_SQL = "INSERT INTO XkcdComics (id, title, alt, img, day, month, year) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String SELECT_XKCD_COMICS_SQL = "SELECT * FROM XkcdComics;";
    private static final String INSERT_WAIFU_SQL = "INSERT INTO Gelbooru (id, tag, title) VALUES (?, ?, ?);";
    private static final String INSERT_BLACKLISTED_USER_SQL = "INSERT INTO Blacklisted (user_id, reason, date) VALUES (?, ?, ?);";
    private static final String SQL = "INSERT INTO Submissions (type, user_id, text, date, guild_id, channel_id) VALUES (?, ?, ?, ?, ?, ?);";

    /**
     * Stores an emote into the database.
     */
    public static void insertEmote(CustomEmoji emote) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_EMOTE_SQL);
        ps.setLong(1, emote.getIdLong());
        ps.setString(2, emote.getName());
        ps.setBoolean(3, emote.isAnimated());
        ps.setString(4, emote.getTimeCreated().toString());
        ps.setString(5, emote.getImageUrl());
        ps.execute();
        ps.close();
        conn.close();
    }

    /**
     * Stores the emotes of the guild into the database.
     */
    public static void insertEmotes(List<RichCustomEmoji> emotes) throws SQLException {
        // Ids of emotes that are already in the database
        List<Long> existing = getEmoteIds();

        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_EMOTE_SQL);
        conn.setAutoCommit(false);

        int batchSize = 100;
        int i = 0;

        for (CustomEmoji emote : emotes) {
            // Skip emotes that are already in the database
            if (existing.contains(emote.getIdLong())) {
                continue;
            }
            // Store emote information
            ps.setLong(1, emote.getIdLong());
            ps.setString(2, emote.getName());
            ps.setBoolean(3, emote.isAnimated());
            ps.setString(4, emote.getTimeCreated().toString());
            ps.setString(5, emote.getImageUrl());
            ps.addBatch();

            if (++i % batchSize == 0) {
                ps.executeBatch();
                conn.commit();
            }
        }
        ps.executeBatch();
        conn.commit();
        ps.close();
        conn.setAutoCommit(true);
        conn.close();
    }

    /**
     * Returns a list of ids of the emotes that are stored in the database.
     */
    public static List<Long> getEmoteIds() throws SQLException {
        String sql = "SELECT emote_id FROM Emotes;";
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();
        List<Long> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        ps.close();
        conn.close();
        return ids;
    }

    /**
     * Inserts a user into the database. Note that this method doesn't check if a user is already in the DB.
     */
    public static void insertUser(User user) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL);
        ps.setLong(1, user.getIdLong());
        ps.setString(2, user.getName());
        ps.setString(3, user.getDiscriminator());
        ps.setBoolean(4, user.isBot());
        ps.execute();
        ps.close();
        conn.close();
    }

    /**
     * Returns a list of ids of the users that are stored in the database.
     */
    public static List<Long> getUserIds() throws SQLException {
        String sql = "SELECT user_id FROM DiscordUsers;";
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();
        List<Long> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        ps.close();
        return ids;
    }

    /**
     * Updates a user in the database.
     */
    public static void updateUser(User user) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(UPDATE_USER_SQL);
        ps.setString(1, user.getName());
        ps.setString(2, user.getDiscriminator());
        ps.setBoolean(3, user.isBot());
        ps.setLong(4, user.getIdLong());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Stores a guild member.
     */
    public static void insertMember(Member member) throws SQLException {
        // Store user in the DB
        if (!hasUser(member.getIdLong())) {
            insertUser(member.getUser());
        }
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_MEMBER_SQL);
        ps.setLong(1, member.getGuild().getIdLong());
        ps.setLong(2, member.getIdLong());
        ps.execute();
        ps.close();
    }

    /**
     * Stores the members of a guild into the database.
     */
    public static void insertMembers(List<Member> members) throws SQLException {
        // Ids of users that are already in the database
        List<Long> existing = getUserIds();

        Connection conn = Database.getConnection();
        PreparedStatement psUser = conn.prepareStatement(INSERT_USER_SQL);
        PreparedStatement psMember = conn.prepareStatement(INSERT_MEMBER_SQL);
        conn.setAutoCommit(false);

        int batchSize = 100;
        int i = 0;

        for (Member member : members) {
            User user = member.getUser();

            if (!existing.contains(user.getIdLong())) {
                // Store user
                psUser.setLong(1, member.getUser().getIdLong());
                psUser.setString(2, member.getUser().getName());
                psUser.setString(3, member.getUser().getDiscriminator());
                psUser.setBoolean(4, member.getUser().isBot());
                psUser.addBatch();
            }
            // Store member
            psMember.setLong(1, member.getGuild().getIdLong());
            psMember.setLong(2, member.getUser().getIdLong());
            psMember.addBatch();

            if (++i % batchSize == 0) {
                psUser.executeBatch();
                psMember.executeBatch();
                conn.commit();
            }
        }
        psUser.executeBatch();
        psMember.executeBatch();
        conn.commit();
        psUser.close();
        psMember.close();
        conn.setAutoCommit(true);

        // TODO: Do it smarter
        deleteDuplicateMemberUsers();
    }

    public static void deleteDuplicateMemberUsers() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(DELETE_DUPLICATE_MEMBERS_SQL);
        ps.execute();
        ps.close();
    }

    /**
     * Removes a member from the DB. In other words, it removes the relation between a user and a guild,
     * but not the user and guild entries themselves.
     */
    public static void deleteMember(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(DELETE_MEMBER_SQL);
        ps.setLong(1, member.getGuild().getIdLong());
        ps.setLong(2, member.getIdLong());
        ps.execute();
        ps.close();
    }

    /**
     * Inserts a guild into the DB.
     */
    public static void insertGuild(Guild guild) throws SQLException {
        if (hasGuild(guild.getIdLong())) {
            return;
        }
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_GUILD_SQL);
        ps.setLong(1, guild.getIdLong());
        ps.setString(2, guild.getName());
        ps.setString(3, guild.getOwnerId());
        ps.execute();
        ps.close();
    }

    /**
     * Updates a guild in the DB.
     */
    public static void updateGuild(Guild guild) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(UPDATE_GUILD_SQL);
        ps.setLong(1, guild.getIdLong());
        ps.setString(2, guild.getName());
        ps.setLong(3, guild.getOwnerIdLong());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Stores the nickname of a member.
     */
    public static void insertNickname(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_NICKNAME_SQL);
        ps.setString(1, member.getNickname());
        ps.setLong(2, member.getIdLong());
        ps.setLong(3, member.getGuild().getIdLong());
        ps.execute();
        ps.close();
    }

    /**
     * Checks if a given user is already in the database.
     *
     * @param userId The id of the Discord user.
     * @return True if the user is already stored in the DB, false otherwise.
     */
    public static boolean hasUser(long userId) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_SQL)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if a given guild is already in the database.
     *
     * @param guildId The id of the Discord guild.
     * @return True if the guild is already stored in the DB, false otherwise.
     */
    public static boolean hasGuild(long guildId) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_GUILD_SQL)) {
            ps.setLong(1, guildId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Returns a list of blocked images from the DB.
     */
    public static List<String> getBlockedImages() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(SELECT_BLOCKED_IMAGE_SQL);
        List<String> result = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            result.add(rs.getString("url"));
        }
        ps.close();
        return result;
    }

    /**
     * Inserts a new blocked image into the DB.
     */
    public static void insertBlockedImage(String image, long userId, String date, String reason) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_BLOCKED_IMAGE_SQL);
        ps.setString(1, image);
        ps.setLong(2, userId);
        ps.setString(3, date);
        ps.setString(4, reason);
        ps.execute();
        ps.close();
    }

    /**
     * Returns a list of xkcd comics from the DB.
     */
    public static List<XkcdComic> getXkcdComics() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(SELECT_XKCD_COMICS_SQL);
        List<XkcdComic> result = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            XkcdComic comic = new XkcdComic();
            comic.setIssueNr(rs.getInt("id"));
            comic.setTitle(rs.getString("title"));
            comic.setImg(rs.getString("img"));
            comic.setAlt(rs.getString("alt"));
            comic.setDate(rs.getInt("day"), rs.getInt("month"), rs.getInt("year"));
            result.add(comic);
        }
        ps.close();
        return result;
    }

    /**
     * Inserts a list of xkcd comics into the DB.
     */
    public static void insertXkcdComics(List<XkcdComic> comics) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_XKCD_COMICS_SQL);
        conn.setAutoCommit(false);
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
        ps.close();
        conn.setAutoCommit(true);
    }

    /**
     * Inserts a waifu into the DB.
     *
     * @param name  Name of the waifu.
     * @param tag   Gelbooru tag of the waifu.
     * @param title Title of the embed.
     */
    public static void insertWaifu(String name, String tag, String title) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_WAIFU_SQL);
        ps.setString(1, name);
        ps.setString(2, tag);
        ps.setString(3, title);
        ps.execute();
        ps.close();
    }

    /**
     * Returns a list of waifu names from the DB.
     */
    public static List<String> getWaifuNames() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM Gelbooru;");
        ResultSet rs = ps.executeQuery();
        List<String> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getString("Id"));
        }
        ps.close();
        return ids;
    }

    /**
     * Returns a set of waifu entries from the DB.
     */
    public static ResultSet getWaifu(String name) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Gelbooru WHERE id = ?;");
        ps.setString(1, name);
        return ps.executeQuery();
    }

    /**
     * Inserts a blacklisted user into the DB.
     *
     * @param userId The Discord id of the {@link User}.
     * @param date   The date when the user was blacklisted.
     * @param reason The reason why the user was blacklisted.
     */
    public static void insertBlacklistedUser(long userId, String date, String reason) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_BLACKLISTED_USER_SQL);
        ps.setLong(1, userId);
        ps.setString(2, reason);
        ps.setString(3, date);
        ps.execute();
        ps.close();
    }

    /**
     * Retrieves a list of blacklisted user ids from the DB.
     *
     * @return A {@link List} of ids of the blacklisted {@link User}s.
     */
    public static List<Long> getBlacklistedUsers() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Blacklisted;");
        ResultSet rs = ps.executeQuery();
        List<Long> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getLong("user_id"));
        }
        ps.close();
        return ids;
    }

    /**
     * Inserts a bug report or a suggestion into the database.
     */
    public static void insertSubmission(Submission submission) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(SQL);
        ps.setString(1, "bug report");
        ps.setString(2, submission.getAuthorId());
        ps.setString(3, submission.getMessage());
        ps.setString(4, submission.getDate());
        ps.setString(5, submission.getGuildId());
        ps.setString(6, submission.getChannelId());
        ps.execute();
        ps.close();
    }
}