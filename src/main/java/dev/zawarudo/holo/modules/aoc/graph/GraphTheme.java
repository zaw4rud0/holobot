package dev.zawarudo.holo.modules.aoc.graph;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public enum GraphTheme {
    AOC("theme_aoc"),
    WOOD("theme_wood");

    private final String name;

    GraphTheme(String name) {
        this.name = name;
    }

    Theme load() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("graph_themes.json");
            if (inputStream == null) {
                throw new IllegalStateException("Failed to load graph_themes.json! Check that the file is at the right location.");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Type listType = new TypeToken<List<Theme>>() {}.getType();
            List<Theme> themes = new Gson().fromJson(reader, listType);

            return themes.stream().filter(t -> name.equals(t.getName())).findFirst().orElseThrow();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load graph_themes.json! Check that the file is at the right location.", e);
        }
    }
}

class Theme {

    @SerializedName("name")
    private String name;

    @SerializedName("background_color")
    private String backgroundColor;
    @SerializedName("text_color")
    private String textColor;
    @SerializedName("grid_color")
    private String gridColor;

    @SerializedName("two_stars_color")
    private String twoStarsColor;
    @SerializedName("one_star_color")
    private String oneStarColor;
    @SerializedName("no_stars_color")
    private String noStarsColor;

    public String getName() {
        return name;
    }

    public Color getBackgroundColor() {
        return Color.decode(backgroundColor);
    }

    public Color getTextColor() {
        return Color.decode(textColor);
    }

    public Color getGridColor() {
        return Color.decode(gridColor);
    }

    public Color getTwoStarsColor() {
        return Color.decode(twoStarsColor);
    }

    public Color getOneStarColor() {
        return Color.decode(oneStarColor);
    }

    public Color getNoStarsColor() {
        return Color.decode(noStarsColor);
    }
}