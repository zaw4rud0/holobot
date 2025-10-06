package dev.zawarudo.holo.database;

import dev.zawarudo.holo.commands.general.CountdownCmd;
import dev.zawarudo.holo.core.GuildConfig;
import dev.zawarudo.holo.modules.xkcd.XkcdComic;
import dev.zawarudo.holo.core.Bootstrap;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that handles all the operations on the database. Note that the connection to the database is automatically established.
 */
public final class DBOperations {

    private static final int BATCH_SIZE = 100;

    private DBOperations() {
    }

    /**
     * Stores one or more emotes into the database.
     */
    public static void insertEmotes(CustomEmoji... emotes) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-emote");
        List<Long> existing = getEmoteIds();

        Connection conn = Database.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 0;

            for (CustomEmoji emote : emotes) {
                if (existing.contains(emote.getIdLong())) {
                    continue;
                }

                ps.setLong(1, emote.getIdLong());
                ps.setString(2, emote.getName());
                ps.setBoolean(3, emote.isAnimated());
                ps.setString(4, emote.getTimeCreated().toString());
                ps.setString(5, emote.getImageUrl());
                ps.addBatch();

                if (++i % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }
            ps.executeBatch();
            conn.commit();
        }
        conn.setAutoCommit(true);
        conn.close();
    }

    /**
     * Returns a list of ids of the emotes that are stored in the database.
     */
    public static List<Long> getEmoteIds() throws SQLException {
        String sql = "SELECT emote_id FROM Emotes;";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            conn.close();
            return ids;
        }
    }

    /**
     * TODO: Implement
     *
     * @param name The name of the emote to search for.
     * @return A list of emotes that fit the specified name.
     */
    public static List<CustomEmoji> getEmotesByName(@NotNull String name) {

        // TODO:
        //  - Use "select-emote-with-suffix.sql"
        //  - Strip suffix and search with just name
        //  - Pick from "display_name" the emote + suffix
        //  - Send "image_url" in channel as webhook

        throw new UnsupportedOperationException("Not implemented yet!");
    }

    /**
     * Inserts a user into the database. Note that this method doesn't check if a user is already in the DB.
     */
    public static void insertUser(User user) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-user");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, user.getIdLong());
            ps.setString(2, user.getName());
            ps.setBoolean(3, user.isBot());
            ps.execute();
        }
        conn.close();
    }

    /**
     * Returns a list of ids of the users that are stored in the database.
     */
    public static List<Long> getUserIds() throws SQLException {
        String sql = "SELECT user_id FROM DiscordUsers;";
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            conn.close();
            return ids;
        }
    }

    /**
     * Updates a user in the database.
     */
    public static void updateUser(User user) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("update-user");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setBoolean(2, user.isBot());
            ps.setLong(3, user.getIdLong());
            ps.executeUpdate();
        }
        conn.close();
    }

    /**
     * Stores a guild member.
     */
    public static void insertMember(Member member) throws SQLException {
        // Store user in the DB
        if (!hasUser(member.getIdLong())) {
            insertUser(member.getUser());
        }

        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-member");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, member.getGuild().getIdLong());
            ps.setLong(2, member.getIdLong());
            ps.execute();
        }
        conn.close();
    }

    /**
     * Stores the members of a guild into the database.
     */
    public static void insertMembers(List<Member> members) throws SQLException {
        String insertUserSql = Bootstrap.holo.getSQLManager().getStatement("insert-user");
        String insertMemberSql = Bootstrap.holo.getSQLManager().getStatement("insert-member");

        // Ids of users that are already in the database
        List<Long> existing = getUserIds();

        Connection conn = Database.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement psUser = conn.prepareStatement(insertUserSql);
             PreparedStatement psMember = conn.prepareStatement(insertMemberSql)) {
            int i = 0;

            for (Member member : members) {
                User user = member.getUser();

                if (!existing.contains(user.getIdLong())) {
                    // Store user
                    psUser.setLong(1, member.getUser().getIdLong());
                    psUser.setString(2, member.getUser().getName());
                    psUser.setString(3, member.getUser().getName());
                    psUser.setBoolean(4, member.getUser().isBot());
                    psUser.addBatch();
                }
                // Store member
                psMember.setLong(1, member.getGuild().getIdLong());
                psMember.setLong(2, member.getUser().getIdLong());
                psMember.addBatch();

                if (++i % BATCH_SIZE == 0) {
                    psUser.executeBatch();
                    psMember.executeBatch();
                    conn.commit();
                }
            }
            psUser.executeBatch();
            psMember.executeBatch();
        }

        conn.commit();
        conn.setAutoCommit(true);
        conn.close();

        deleteDuplicateMemberUsers();
    }

    public static void deleteMembers(List<Member> members) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("delete-member");

        Connection conn = Database.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 0;

            for (Member member : members) {
                ps.setLong(1, member.getGuild().getIdLong());
                ps.setLong(2, member.getUser().getIdLong());
                ps.addBatch();

                if (++i % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }
            ps.executeBatch();
        }
        conn.commit();
        conn.setAutoCommit(true);
        conn.close();
    }

    public static void deleteDuplicateMemberUsers() throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("delete-duplicate-members");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        }
        conn.close();
    }

    /**
     * Removes a member from the DB. In other words, it removes the relation between a user and a guild,
     * but not the user and guild entries themselves.
     */
    public static void deleteMember(Member member) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("delete-member");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, member.getGuild().getIdLong());
            ps.setLong(2, member.getIdLong());
            ps.execute();
        }
        conn.close();
    }

    /**
     * Inserts a guild into the DB.
     */
    public static void insertGuild(Guild guild) throws SQLException {
        if (hasGuild(guild.getIdLong())) {
            return;
        }

        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-guild");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, guild.getName());
            ps.execute();
        }
        conn.close();
    }

    /**
     * Updates a guild in the DB.
     */
    public static void updateGuild(Guild guild) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("update-guild");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, guild.getName());
            ps.executeUpdate();
        }
        conn.close();
    }

    public static void deleteGuild(Guild guild) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("delete-guild");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        }
        conn.close();
    }

    /**
     * Stores the nickname of a member.
     */
    public static void insertNickname(Member member) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-nickname");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getNickname());
            ps.setLong(2, member.getIdLong());
            ps.setLong(3, member.getGuild().getIdLong());
            ps.execute();
        }
        conn.close();
    }

    /**
     * Checks if a given user is already in the database.
     *
     * @param userId The id of the Discord user.
     * @return True if the user is already stored in the DB, false otherwise.
     */
    public static boolean hasUser(long userId) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-user");

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-guild");

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-all-blocked-images");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            List<String> result = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("url"));
            }
            conn.close();
            return result;
        }
    }

    /**
     * Inserts a new blocked image into the DB.
     */
    public static void insertBlockedImage(String image, long userId, String date, String reason) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-blocked-image");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, image);
            ps.setLong(2, userId);
            ps.setString(3, date);
            ps.setString(4, reason);
            ps.execute();
        }
        conn.close();
    }

    /**
     * Returns a list of xkcd comics from the DB.
     */
    public static List<XkcdComic> getXkcdComics() throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-all-xkcd-comics");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            conn.close();
            return result;
        }
    }

    /**
     * Inserts a list of xkcd comics into the DB.
     */
    public static void insertXkcdComics(List<XkcdComic> comics) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-xkcd-comic");

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

    /**
     * Inserts a waifu into the DB.
     *
     * @param name  Name of the waifu.
     * @param tag   Gelbooru tag of the waifu.
     * @param title Title of the embed.
     */
    public static void insertWaifu(String name, String tag, String title) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-waifu");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, tag);
            ps.setString(3, title);
            ps.execute();
        }
    }

    /**
     * Returns a list of waifu names from the DB.
     */
    public static List<String> getWaifuNames() throws SQLException {
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM Gelbooru;")) {
            ResultSet rs = ps.executeQuery();
            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString("Id"));
            }
            return ids;
        }
    }

    /**
     * Returns a set of waifu entries from the DB.
     */
    public static ResultSet getWaifu(String name) throws SQLException {

        // TODO: Return a list of waifu objects instead of a ResultSet.
        //  That should decrease dependency and cohesion

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
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-blacklisted-user");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, reason);
            ps.setString(3, date);
            ps.execute();
        }
    }

    /**
     * Retrieves a list of blacklisted user ids from the DB.
     *
     * @return A {@link List} of ids of the blacklisted {@link User}s.
     */
    public static List<Long> getBlacklistedUsers() throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-all-blacklisted-users");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong("user_id"));
            }
            return ids;
        }
    }

    /**
     * Stores a countdown instance in the database.
     *
     * @param countdown The countdown instance with all the information.
     * @throws SQLException When something went wrong while inserting the countdown into the database.
     */
    public static void insertCountdown(CountdownCmd.Countdown countdown) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-countdown");
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, countdown.name());
            ps.setLong(2, countdown.timeCreated());
            ps.setLong(3, countdown.dateTime());
            ps.setLong(4, countdown.userId());
            ps.setLong(5, countdown.serverId());
            ps.execute();
        }
        conn.close();
    }

    /**
     * Removes a given countdown from the database.
     *
     * @param countdownId The id of the countdown to be deleted.
     * @throws SQLException When something went wrong while removing the countdown from the database.
     */
    public static void deleteCountdown(long countdownId) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("delete-countdown");
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, countdownId);
            ps.execute();
        }
    }

    /**
     * Retrieves all countdowns of a given user from the database.
     *
     * @param userId The id of the given user.
     * @return All the countdown instances created by the given user.
     * @throws SQLException When something wrong while fetching the countdowns from the database.
     */
    public static List<CountdownCmd.Countdown> fetchCountdowns(long userId) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-countdown");

        Connection conn = Database.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            List<CountdownCmd.Countdown> countdowns = new ArrayList<>();

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                long timeCreated = rs.getLong("time_created");
                long dateTime = rs.getLong("date_time");
                long serverId = rs.getLong("guild_id");

                CountdownCmd.Countdown countdown = new CountdownCmd.Countdown(id, name, timeCreated, dateTime, userId, serverId);
                countdowns.add(countdown);
            }
            conn.close();
            return countdowns;
        }
    }

    public static void insertGuildConfig(GuildConfig config) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("insert-guild-config.sql");
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, config.getGuildId());
            ps.setString(2, config.getPrefix());
            ps.setBoolean(3, config.isNSFWEnabled());
            ps.execute();
        }
    }

    public static Map<Long, GuildConfig> selectGuildConfigs() throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("select-all-guild-configs");

        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Map<Long, GuildConfig> map = new HashMap<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long guildId = rs.getLong("guild_id");
                GuildConfig config = new GuildConfig(guildId);
                config.setAllowNSFW(rs.getBoolean("nsfw"));
                config.setPrefix(rs.getString("prefix"));
                map.put(guildId, config);
            }
            conn.close();
            return map;
        }
    }

    public static void updateGuildConfig(GuildConfig config) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("update-guild-config.sql");
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, config.getPrefix());
            ps.setBoolean(2, config.isNSFWEnabled());
            ps.setLong(3, config.getGuildId());
            ps.executeUpdate();
            conn.close();
        }
    }

    public static void deleteGuildConfig(GuildConfig config) throws SQLException {
        String sql = Bootstrap.holo.getSQLManager().getStatement("delete-guild-config.sql");
        Connection conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, config.getGuildId());
            ps.execute();
        }
        conn.close();
    }

    public static void createNewDatabase() throws SQLException {
        String script = Bootstrap.holo.getSQLManager().getStatement("create-database");
        try (Connection conn = Database.getConnection()) {
            runSqlScript(conn, script);
        }
    }

    private static void runSqlScript(Connection conn, String script) throws SQLException {
        // Optional but recommended for SQLite
        try (var pragma = conn.createStatement()) {
            pragma.execute("PRAGMA foreign_keys = ON");
        }

        java.util.List<String> statements = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingle = false, inDouble = false;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
            } else if (c == '"' && !inSingle) {
                inDouble = !inDouble;
            }

            if (c == ';' && !inSingle && !inDouble) {
                String stmt = current.toString().trim();
                if (!stmt.isBlank()) statements.add(stmt);
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        String tail = current.toString().trim();
        if (!tail.isBlank()) statements.add(tail);

        // Execute all statements in a transaction
        boolean origAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (var st = conn.createStatement()) {
            for (String sql : statements) {
                // skip single-line comments (optional)
                String trimmed = sql.replaceAll("(?m)^\\s*--.*$", "").trim();
                if (!trimmed.isBlank()) st.execute(trimmed);
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(origAuto);
        }
    }
}