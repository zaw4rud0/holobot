package dev.zawarudo.holo.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.utils.Reader;
import dev.zawarudo.holo.utils.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Main class of the application.
 */
public final class Bootstrap {

    /**
     * An instance of the Holo class which represents the bot.
     */
    public static Holo holo;
    /**
     * The exact time at which the bot started up in milliseconds.
     */
    private static long startupTime;
    /**
     * The path to the config file.
     */
    private static final String CONFIG_PATH = "config.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    private Bootstrap() {
    }

    public static void main(String[] args) {
        init();
    }

    private static void init() {
        startupTime = System.currentTimeMillis();

        try {
            BotConfig botConfig = initializeConfig();
            holo = new Holo(botConfig);
        } catch (IOException ex) {
            LOGGER.error("Something went while reading and initializing the bot with the configurations.",ex);
            return;
        }

        checkDatabase();

        if (LOGGER.isInfoEnabled()) {
            long totalTime = System.currentTimeMillis() - startupTime;
            LOGGER.info(String.format("It took %s %d ms to load!", holo.getJDA().getSelfUser().getName(), totalTime));
        }
    }

    /**
     * Deserializes the config file to a {@link BotConfig} object.
     *
     * @return A {@link BotConfig} object.
     */
    private static BotConfig initializeConfig() throws IOException {
        File configFile = new File(CONFIG_PATH);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!configFile.exists()) {
            BotConfig placeholderConfig = new BotConfig();
            JsonObject jsonConfig = gson.toJsonTree(placeholderConfig).getAsJsonObject();

            Writer.writeToFile(jsonConfig, CONFIG_PATH);

            LOGGER.error("Configuration file 'config.json' not found. A new file has been created with placeholder values. Please configure the file and restart the program.");
            System.exit(0);
        }

        JsonObject obj = Reader.readJsonObject(CONFIG_PATH);
        return new Gson().fromJson(obj, BotConfig.class);
    }

    private static void checkDatabase() {
        File dbFile = new File("Holo.db");
        if (!dbFile.exists()) {
            try {
                DBOperations.createNewDatabase();
            } catch (SQLException ex) {
                throw new IllegalStateException("Something went wrong while creating the missing database.", ex);
            }
        }

        // TODO: Check if the database has all the needed tables and columns
    }

    /**
     * Restarts the bot.
     */
    public static void restart() {
        LOGGER.info("Restarting...");
        init();
    }

    /**
     * Shuts the bot down.
     */
    public static void shutdown() {
        LOGGER.info("Shutting down...");
        holo.getJDA().shutdown();
    }

    public static long getStartupTime() {
        return startupTime;
    }
}