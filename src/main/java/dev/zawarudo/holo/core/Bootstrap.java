package dev.zawarudo.holo.core;

import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.utils.VersionInfo;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;

import static dev.zawarudo.holo.core.BotConfig.mask;

/**
 * Main class of the application.
 */
public final class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);
    private static Dotenv dotenv;

    /**
     * The exact time at which the bot started up in milliseconds.
     */
    private static long startupTime;

    /**
     * The running bot instance (initialized in {@link #init()}).
     */
    public static Holo holo;

    private Bootstrap() {
    }

    public static void main(String[] args) {
        init();
    }

    private static void init() {
        startupTime = System.currentTimeMillis();

        dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        LOGGER.info(".env loaded ({} entries)", dotenv.entries().spliterator().getExactSizeIfKnown());
        LOGGER.info("BOT_TOKEN: {}", mask(dotenv.get("BOT_TOKEN")));
        LOGGER.info("OWNER_ID: {}", dotenv.get("OWNER_ID"));

        String version = VersionInfo.getVersion();
        LOGGER.info("Version: {}", version);

        BotConfig config = buildConfig(dotenv);

        holo = new Holo(config);
        holo.registerEarlyManagers();

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
    private static BotConfig buildConfig(Dotenv env) {
        return BotConfig.builder()
                .botToken(require(env, "BOT_TOKEN"))
                .ownerId(requireLong(env, "OWNER_ID"))
                .defaultPrefix(env.get("DEFAULT_PREFIX", "<"))
                .deepAIKey(env.get("DEEP_AI_TOKEN", ""))
                .aocToken(env.get("AOC_TOKEN", ""))
                .saucenaoToken(env.get("SAUCE_NAO_TOKEN", ""))
                .githubToken(env.get("GITHUB_TOKEN", ""))
                .dictionaryKey(env.get("KEY_DICTIONARY", ""))
                .thesaurusKey(env.get("KEY_THESAURUS", ""))
                .build();
    }

    private static void checkDatabase() {
        String dbPath = dotenv.get("DB_PATH", "./data/holobot.db");
        Database.setDbPath((dbPath));

        File dbFile = new File(dbPath);

        if (!dbFile.exists()) {
            try {
                DBOperations.createNewDatabase();
                LOGGER.info("Created new database at {}", dbFile.getAbsolutePath());
            } catch (SQLException ex) {
                throw new IllegalStateException("Failed to create database at " + dbFile.getAbsolutePath(), ex);
            }
        } else {
            LOGGER.info("Using existing database at {}", dbFile.getAbsolutePath());
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

    private static String require(Dotenv env, String name) {
        String val = env.get(name);
        if (val == null || val.isBlank()) {
            LOGGER.error("Missing required environment variable: {}", name);
            System.exit(1);
        }
        return val;
    }

    private static long requireLong(Dotenv env, String name) {
        String val = require(env, name);
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            LOGGER.error("{} must be a valid long, but got '{}'", name, val);
            System.exit(1);
            throw e;
        }
    }
}