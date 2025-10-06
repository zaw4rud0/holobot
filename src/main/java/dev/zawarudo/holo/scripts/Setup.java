package dev.zawarudo.holo.scripts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Simple script to be run after cloning to ensure all needed files are in place. If
 * a file is missing, it will be created.
 */
public class Setup {

    private static final Logger LOGGER = LoggerFactory.getLogger(Setup.class);

    public static void main(String[] args) throws IOException {
        logInfo("Running setup...");
        createEnvIfMissing();
        createDatabaseIfMissing();
    }

    private static void createEnvIfMissing() throws IOException {
        File envFile = new File(".env");
        File exampleFile = new File(".env.example");

        if (!envFile.exists()) {
            if (!exampleFile.exists()) {
                logError("Missing .env and .env.example — cannot create configuration automatically!");
                return;
            }

            logInfo(".env file is missing. Creating a new one from .env.example...");
            Files.copy(exampleFile.toPath(), envFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logInfo("Created new .env file. Please edit it and fill in your actual tokens and API keys.");
        } else {
            logInfo(".env file found. Using existing configuration.");
        }
    }

    private static void createDatabaseIfMissing() {
        File file = new File("src/main/resources/database/holobot.db");
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

    private static void logError(String errorString) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(errorString);
        }
    }
}