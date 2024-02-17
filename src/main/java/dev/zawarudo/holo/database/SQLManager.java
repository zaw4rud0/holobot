package dev.zawarudo.holo.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SQLManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLManager.class);
    private static final Path SQL_DIRECTORY_PATH = Path.of("./src/main/resources/database/SQL/");
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
        String adjustedName = name.endsWith(".sql") ? name.substring(0, name.length() - 4) : name;
        if (!sqlStatements.containsKey(adjustedName)) {
            throw new IllegalArgumentException("There is no SQL file with name: " + name);
        }
        return sqlStatements.get(name);
    }

    private Map<String, String> loadSQLStatements() throws IOException {
        Map<String, String> statements = new HashMap<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(SQL_DIRECTORY_PATH, "*.sql")) {
            for (Path filePath : directoryStream) {
                String name = getFileNameWithoutExtension(filePath);
                String content = Files.readString(filePath);
                statements.put(name, content);
            }
        }
        return statements;
    }

    private String getFileNameWithoutExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }
}