package dev.zawarudo.holo.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SQLManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLManager.class);
    private static final String SQL_DIRECTORY_PATH = "/dev/zawarudo/holo/database";
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
        String formattedName = name.replace(" ", "-").replaceAll("\\.sql$", "");
        if (!sqlStatements.containsKey(formattedName)) {
            throw new IllegalArgumentException("There is no SQL file with name: " + formattedName);
        }
        return sqlStatements.get(formattedName);
    }

    private Map<String, String> loadSQLStatements() throws IOException {
        Map<String, String> statements = new HashMap<>();

        try (InputStream is = getClass().getResourceAsStream(SQL_DIRECTORY_PATH)) {
            if (is == null) {
                throw new IOException("Resource directory not found: " + SQL_DIRECTORY_PATH);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String resourceName;
            while ((resourceName = reader.readLine()) != null) {
                if (resourceName.endsWith(".sql")) {
                    String content = loadResourceContent(SQL_DIRECTORY_PATH + "/" + resourceName);
                    statements.put(resourceName.replace(".sql", ""), content);
                }
            }
        }
        return statements;
    }

    private String loadResourceContent(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource file not found: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}