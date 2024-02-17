package dev.zawarudo.holo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class for working with files and directories.
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Fetches all files inside the given directory path.
     *
     * @param directoryPath The path to the directory from which the files should be fetched.
     * @return All files inside the given directory path, including those inside subdirectories.
     */
    public static List<File> getAllFiles(String directoryPath) {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to walk through directory.", e);
        }
    }
}