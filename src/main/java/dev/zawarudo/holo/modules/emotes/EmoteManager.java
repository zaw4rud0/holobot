package dev.zawarudo.holo.modules.emotes;

import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.database.Database;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO:
//  - How to handle emote names that overlap command names, command aliases, or actions

/**
 * Manages operations related to custom emotes, including integrity checks, insertion, and retrieval.
 * This class ensures unique naming and maintains data consistency in the emote database.
 */
public class EmoteManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmoteManager.class);

    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*?)(-\\d+)?$");

    private static final String EMOTE_ID_COL_NAME = "emote_id";
    private static final String EMOTE_NAME_COL_NAME = "emote_name";
    private static final String IS_ANIMATED_COL_NAME = "is_animated";

    /**
     * Initializes the EmoteManager and performs an integrity check on the emote database.
     */
    public EmoteManager() {
        try {
            performIntegrityCheck2();
        } catch (SQLException ex) {
            LOGGER.error("Something went wrong while performing an integrity check on the database!", ex);
        }
    }

    /**
     * Performs an integrity check on the Emotes table, ensuring all names are unique.
     */
    private void performIntegrityCheck2() throws SQLException {
        Map<String, List<EmoteRecord>> duplicateGroups = findDuplicateEmotes();

        if (duplicateGroups.isEmpty()) {
            LOGGER.info("Emote data integrity check passed.");
            return;
        }

        List<EmoteUpdate> updates = resolveDuplicates(duplicateGroups);
        if (!updates.isEmpty()) {
            updateEmoteNames(updates);
            LOGGER.info("Resolved {} duplicate emotes names.", updates.size());
        }
    }

    private Map<String, List<EmoteRecord>> findDuplicateEmotes() throws SQLException {
        String query = """
                    SELECT emote_id, emote_name, LOWER(emote_name) AS base_name
                    FROM Emotes
                    WHERE LOWER(emote_name) IN (
                        SELECT LOWER(emote_name)
                        FROM Emotes
                        GROUP BY LOWER(emote_name)
                        HAVING COUNT(*) > 1
                    )
                    ORDER BY base_name, emote_id;
                """;

        Map<String, List<EmoteRecord>> duplicateGroups = new LinkedHashMap<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String baseName = rs.getString("base_name");
                EmoteRecord emoteRecord = new EmoteRecord(
                        rs.getLong(EMOTE_ID_COL_NAME),
                        rs.getString(EMOTE_NAME_COL_NAME)
                );

                duplicateGroups.computeIfAbsent(baseName, k -> new ArrayList<>()).add(emoteRecord);
            }
        }
        return duplicateGroups;
    }

    private List<EmoteUpdate> resolveDuplicates(Map<String, List<EmoteRecord>> duplicateGroups) {
        List<EmoteUpdate> updates = new ArrayList<>();

        for (Map.Entry<String, List<EmoteRecord>> entry : duplicateGroups.entrySet()) {
            List<EmoteRecord> duplicates = entry.getValue();
            duplicates.sort(Comparator.comparingLong(EmoteRecord::emoteId));

            boolean first = true;
            int suffix = 1;

            for (EmoteRecord emoteRecord : duplicates) {
                String newName = first ? entry.getKey() : entry.getKey() + "-" + suffix++;
                first = false;

                if (!emoteRecord.emoteName().equals(newName)) {
                    updates.add(new EmoteUpdate(emoteRecord.emoteId(), newName));
                }
            }
        }
        return updates;
    }

    private void updateEmoteNames(List<EmoteUpdate> updates) throws SQLException {
        String query = "UPDATE Emotes SET emote_name = ? WHERE emote_id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            for (EmoteUpdate update : updates) {
                ps.setString(1, update.uniqueName);
                ps.setLong(2, update.emoteId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Inserts one or more custom emotes into the database.
     * Ensures that each emote has a unique name and ID.
     *
     * @param emotes An array of {@link CustomEmoji} objects to insert.
     */
    public synchronized void insertEmotes(CustomEmoji... emotes) throws SQLException {
        String query = Bootstrap.holo.getSQLManager().getStatement("insert-emote");

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            conn.setAutoCommit(false);

            for (CustomEmoji emote : emotes) {
                if (isEmoteIdInDatabase(emote.getIdLong())) {
                    continue;
                }

                String uniqueName = generateUniqueName(emote.getName());

                ps.setLong(1, emote.getIdLong());
                ps.setString(2, uniqueName);
                ps.setBoolean(3, emote.isAnimated());
                ps.setString(4, emote.getTimeCreated().toString());
                ps.setString(5, emote.getImageUrl());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    /**
     * Searches for emotes with names that partially match the specified search term.
     *
     * @param name The search term to match emote names against.
     * @return A list of {@link CustomEmoji} objects matching the search criteria.
     */
    public List<CustomEmoji> searchEmotesByName(String name) throws SQLException {
        String query = "SELECT emote_id, emote_name, is_animated FROM Emotes WHERE emote_name LIKE ?;";
        List<CustomEmoji> results = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new CustomEmojiImpl(
                            rs.getString(EMOTE_NAME_COL_NAME),
                            rs.getLong(EMOTE_ID_COL_NAME),
                            rs.getBoolean(IS_ANIMATED_COL_NAME)
                    ));
                }
            }
        }
        return results;
    }

    /**
     * Retrieves an emote by its exact name.
     *
     * @param name The exact name of the emote to retrieve.
     * @return An {@link Optional} containing the {@link CustomEmoji} if found, or empty if not found.
     */
    public Optional<CustomEmoji> getEmoteByName(String name) throws SQLException {
        String query = """
                        SELECT emote_id, emote_name, is_animated
                        FROM Emotes
                        WHERE LOWER(emote_name) = LOWER(?);
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CustomEmojiImpl(
                            rs.getString(EMOTE_NAME_COL_NAME),
                            rs.getLong(EMOTE_ID_COL_NAME),
                            rs.getBoolean(IS_ANIMATED_COL_NAME)
                    ));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Renames a specified emote in the database. If the new name is already in use, then the two emotes change their names.
     *
     * @param emote   The emote to be renamed.
     * @param newName The new name the emote should have.
     */
    public void renameEmote(String emote, String newName) throws SQLException {
        String getEmoteQuery = "SELECT emote_id FROM Emotes WHERE LOWER(emote_name) = LOWER(?);";
        String getConflictingEmoteQuery = "SELECT emote_id, emote_name FROM Emotes WHERE LOWER(emote_name) = LOWER(?);";
        String updateEmoteQuery = "UPDATE Emotes SET emote_name = ? WHERE emote_id = ?;";

        try (Connection conn = Database.getConnection();
             PreparedStatement getEmoteStmt = conn.prepareStatement(getEmoteQuery);
             PreparedStatement getConflictStmt = conn.prepareStatement(getConflictingEmoteQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateEmoteQuery)) {

            conn.setAutoCommit(false);

            // Step 1: Check if the emote exists
            getEmoteStmt.setString(1, emote);
            ResultSet emoteResult = getEmoteStmt.executeQuery();

            if (!emoteResult.next()) {
                LOGGER.info("Emote '{}' does not exist. No changes made.", emote);
                return;
            }

            long emoteId = emoteResult.getLong(EMOTE_ID_COL_NAME);

            // Step 2: Check if newName is already in use
            getConflictStmt.setString(1, newName);
            ResultSet conflictResult = getConflictStmt.executeQuery();

            if (conflictResult.next()) {
                long conflictingEmoteId = conflictResult.getLong(EMOTE_ID_COL_NAME);
                String conflictingEmoteName = conflictResult.getString(EMOTE_NAME_COL_NAME);

                // Swap names between the two emotes
                updateStmt.setString(1, conflictingEmoteName);
                updateStmt.setLong(2, emoteId);
                updateStmt.addBatch();

                updateStmt.setString(1, newName);
                updateStmt.setLong(2, conflictingEmoteId);
                updateStmt.addBatch();

                updateStmt.executeBatch();
                conn.commit();

                LOGGER.info("Swapped names: '{}' <-> '{}'.", emote, newName);
            } else {
                // Simply rename the emote
                updateStmt.setString(1, newName);
                updateStmt.setLong(2, emoteId);
                updateStmt.executeUpdate();
                conn.commit();

                LOGGER.info("Renamed emote '{}' to '{}'.", emote, newName);
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to rename emote '{}' to '{}'. Rolling back changes.", emote, newName, e);
            throw e;
        }
    }

    /**
     * Checks if an emote ID exists in the database.
     */
    private boolean isEmoteIdInDatabase(long emoteId) throws SQLException {
        String query = "SELECT 1 FROM Emotes WHERE emote_id = ?;";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, emoteId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Generates a unique name for the emote, ensuring no duplicates in the database.
     */
    private String generateUniqueName(String baseName) throws SQLException {
        String strippedName = stripSuffix(baseName);

        String query = "SELECT emote_name FROM Emotes WHERE LOWER(emote_name) LIKE ?;";
        Set<String> existingNames = new HashSet<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, strippedName.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    existingNames.add(rs.getString(EMOTE_NAME_COL_NAME).toLowerCase());
                }
            }
        }

        int suffix = 1;
        String uniqueName = strippedName;

        while (existingNames.contains(uniqueName)) {
            uniqueName = strippedName + "-" + suffix++;
        }
        return uniqueName;
    }

    /**
     * Strips the numeric suffix from a name, if present.
     */
    private String stripSuffix(String name) {
        Matcher matcher = SUFFIX_PATTERN.matcher(name);
        return matcher.matches() ? matcher.group(1) : name;
    }

    private record EmoteRecord(long emoteId, String emoteName) {
    }

    private record EmoteUpdate(long emoteId, String uniqueName) {
    }
}
