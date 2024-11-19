package dev.zawarudo.holo.scripts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simple script to be run after cloning to ensure all needed files are in place. If
 * a file is missing, it will be created.
 */
public class Setup {

    private static final Logger LOGGER = LoggerFactory.getLogger(Setup.class);

    public static void main(String[] args) throws IOException {
        logInfo("Running setup...");
        createConfigIfMissing();
        createDatabaseIfMissing();
    }

    private static void createConfigIfMissing() throws IOException {
        // TODO: Make copy of .env.example instead

        File file = new File("config.json");
        if (!file.exists()) {
            logInfo("Config file is missing. Creating a new one with default settings...");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(getDefaultConfigString());
            }
            logInfo("Created a new config file. Please open it and replace the placeholder values.");
        }
    }

    @Deprecated
    private static String getDefaultConfigString() {
        return """
                {
                    "token" : "BOT_TOKEN",
                	"owner_id" : OWNER_ID,
                    "default_prefix" : ",",
                    "version" : "VERSION",
                    "deepAI_token" : "DEEPAI_TOKEN",
                    "aoc_token" : "AOC_TOKEN"
                }
                """;
    }

    private static void createDatabaseIfMissing() {
        File file = new File("src/main/resources/database/Holo.db");
        if (!file.exists()) {
            logInfo("Database is missing. Creating a new one...");

            // TODO: Create a new database with the same structure (maybe template)
        }
    }

    private static void logInfo(String infoString) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(infoString);
        }
    }
}