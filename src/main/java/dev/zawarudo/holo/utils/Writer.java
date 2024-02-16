package dev.zawarudo.holo.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Writer {

    private Writer() {
    }

    public static void writeToFile(JsonObject obj, String filepath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filepath))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(obj, writer);
        }
    }

    public static void writeToFile(JsonArray array, String filepath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filepath))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);
        }
    }

    public static void writeToFile(String content, String filepath) throws IOException {
        Files.writeString(Paths.get(filepath), content);
    }
}