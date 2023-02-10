package dev.zawarudo.holo.scripts;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class EmoteSaver {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmoteSaver.class);

    private EmoteSaver() {
    }

    public static void main(String[] args) throws IOException, SQLException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("./src/main/resources/misc/emote-info.json"));
        Type type = new TypeToken<List<EmoteInfo>>() {
        }.getType();
        List<EmoteInfo> emotes = new Gson().fromJson(reader, type);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} emotes loaded from Json file!", emotes.size());
        }
        insertEmotes(emotes);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Emote data inserted into database!");
        }
    }

    public static void insertEmotes(List<EmoteInfo> emotes) throws SQLException {
        long start1 = System.currentTimeMillis();
        List<Long> existing = DBOperations.getEmoteIds();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Found {} emotes in the database. ({}ms)", existing.size(), System.currentTimeMillis() - start1);
        }

        if (emotes.size() == existing.size()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("No new emotes to insert!");
            }
            return;
        }

        int batchSize = 1000000;
        String sql = "INSERT INTO Emotes (emote_id, emote_name, is_animated, created_at, image_url) VALUES (?, ?, ?, ?, ?)";
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        conn.setAutoCommit(false);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Starting to insert the emotes into the database...");
        }

        int i = 0;
        for (EmoteInfo emote : emotes) {
            // Skip existing emotes
            if (existing.contains(emote.id)) {
                continue;
            }
            i++;
            ps.setLong(1, emote.id);
            ps.setString(2, emote.name);
            ps.setBoolean(3, emote.animated);
            ps.setString(4, emote.createdAt);
            ps.setString(5, emote.url);
            ps.addBatch();

            if (i % batchSize == 0) {
                long start = System.currentTimeMillis();
                ps.executeBatch();
                conn.commit();
                LOGGER.info("{} emotes inserted in {}ms", i % batchSize, System.currentTimeMillis() - start);
            }
        }
        long start2 = System.currentTimeMillis();
        ps.executeBatch();
        conn.commit();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} emotes inserted in {}ms", i % batchSize, System.currentTimeMillis() - start2);
        }
        ps.close();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("All emotes inserted!");
        }
    }

    private static class EmoteInfo {
        @SerializedName("Id")
        public long id;
        @SerializedName("Name")
        public String name;
        @SerializedName("Animated")
        public boolean animated;
        @SerializedName("CreatedAt")
        public String createdAt;
        @SerializedName("Url")
        public String url;
    }
}