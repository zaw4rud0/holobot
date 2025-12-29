package dev.zawarudo.holo.database;

import dev.zawarudo.holo.core.Bootstrap;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

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

    private static final int BATCH_SIZE = 100;

    private DBOperations() {
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
}