package dev.zawarudo.holo.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.utils.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(ex.getMessage(), ex);
            }
            return;
        }

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
        JsonObject obj = Reader.readJsonObject("config.json");
        return new Gson().fromJson(obj, BotConfig.class);
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