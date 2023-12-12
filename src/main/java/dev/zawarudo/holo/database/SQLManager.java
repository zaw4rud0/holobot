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

    public SQLManager() throws IOException {
        sqlStatements = loadSQLStatements();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Loaded {} SQL statements!", sqlStatements.size());
        }
    }

    public String getStatement(String name) {
        return sqlStatements.get(name);
    }

    private Map<String, String> loadSQLStatements() throws IOException {
        Map<String, String> sqlStatementsMap = new HashMap<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(SQL_DIRECTORY_PATH, "*.sql")) {
            for (Path filePath : directoryStream) {
                String name = getFileNameWithoutExtension(filePath);
                String content = Files.readString(filePath);
                sqlStatementsMap.put(name, content);
            }
        }
        return sqlStatementsMap;
    }

    private String getFileNameWithoutExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }
}