package dev.zawarudo.holo.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for working with files and directories.
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Fetches all files inside the given directory path.
     *
     * @param directoryPath The directoryPath to the directory from which the files should be fetched.
     * @return All files inside the given directory path, including those inside subdirectories.
     */
    public static List<File> getAllFiles(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Input directoryPath must be a directory.");
        }

        List<File> files = new ArrayList<>();

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                files.add(file);
            } else {
                files.addAll(getAllFiles(file.getAbsolutePath()));
            }
        }
        return files;
    }
}
