package dev.zawarudo.holo.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class SQLManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLManager.class);
    private static final String SQL_DIRECTORY_PATH = "dev/zawarudo/holo/database";
    private final Map<String, String> sqlStatements;

    /**
     * Initializes the SQLManager by loading SQL statements from the defined directory.
     *
     * @throws IOException If there is an issue accessing or reading the SQL files.
     */
    public SQLManager() throws IOException {
        sqlStatements = loadSQLStatements();
        LOGGER.info("Loaded {} SQL statements!", sqlStatements.size());
    }

    /**
     * Retrieves a SQL statement by its name.
     *
     * @param name The name of the SQL file, with or without the ".sql" extension.
     * @return The SQL statement as a string.
     * @throws IllegalArgumentException If there is no SQL statement with the given name.
     */
    public String getStatement(String name) {
        String formattedName = formatFileName(name);
        if (!sqlStatements.containsKey(formattedName)) {
            throw new IllegalArgumentException("No SQL file found with name: " + formattedName);
        }
        return sqlStatements.get(formattedName);
    }

    /**
     * Loads SQL statements from the defined directory, handling both IDE and Jar scenarios.
     *
     * @return A map of file names to their SQL content.
     */
    private Map<String, String> loadSQLStatements() throws IOException {
        URL resourceURL = getClass().getClassLoader().getResource(SQL_DIRECTORY_PATH);
        if (resourceURL == null) {
            throw new IOException("SQL directory not found: " + SQL_DIRECTORY_PATH);
        }

        if ("file".equals(resourceURL.getProtocol())) {
            LOGGER.info("Loading SQL files from file system...");
            return loadFromDirectory(new File(resourceURL.getPath()));
        } else if ("jar".equals(resourceURL.getProtocol())) {
            LOGGER.info("Loading SQL files from JAR...");
            return loadFromJar(resourceURL);
        } else {
            throw new IOException("Unsupported protocol for resource URL: " + resourceURL);
        }
    }

    /**
     * Loads SQL files from a directory in the file system.
     *
     * @param directory The directory to scan.
     * @return A map of file names to their SQL content.
     */
    private Map<String, String> loadFromDirectory(File directory) throws IOException {
        Map<String, String> statements = new HashMap<>();

        if (!directory.isDirectory()) {
            throw new IOException("Expected a directory but found: " + directory.getPath());
        }

        File[] sqlFiles = directory.listFiles((dir, name) -> name.endsWith(".sql"));
        if (sqlFiles == null) {
            throw new IOException("Failed to list files in directory: " + directory.getPath());
        }

        for (File file : sqlFiles) {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            statements.put(formatFileName(file.getName()), content);
            LOGGER.debug("Loaded SQL file: {}", file.getName());
        }
        return statements;
    }


    private Map<String, String> loadFromJar(URL resourceURL) throws IOException {
        Map<String, String> statements = new HashMap<>();

        JarURLConnection jarConnection = (JarURLConnection) resourceURL.openConnection();
        JarFile jarFile = jarConnection.getJarFile();

        jarFile.stream()
                .filter(entry -> entry.getName().startsWith(SQL_DIRECTORY_PATH) && entry.getName().endsWith(".sql"))
                .forEach(entry -> {
                    try (InputStream is = getClass().getClassLoader().getResourceAsStream(entry.getName())) {
                        if (is != null) {
                            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                            String fileName = entry.getName().substring(entry.getName().lastIndexOf('/') + 1);
                            statements.put(formatFileName(fileName), content);
                            LOGGER.debug("Loaded SQL file: {}", fileName);
                        }
                    } catch (IOException e) {
                        LOGGER.error("Failed to load SQL file: {}", entry.getName(), e);
                    }
                });

        return statements;
    }

    /**
     * Formats a file name by removing the ".sql" extension and replacing spaces with hyphens.
     *
     * @param fileName The original file name.
     * @return The formatted file name.
     */
    private String formatFileName(String fileName) {
        return fileName.replace(" ", "-").replaceAll("\\.sql$", "");
    }
}