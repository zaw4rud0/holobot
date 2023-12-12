package dev.zawarudo.holo.scripts;

import dev.zawarudo.nanojikan.JikanAPI;
import dev.zawarudo.nanojikan.exception.APIException;
import dev.zawarudo.nanojikan.exception.InvalidIdException;
import dev.zawarudo.nanojikan.model.Anime;
import dev.zawarudo.nanojikan.model.Season;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnimeSeasonGrid {

    public static void main(String[] args) throws APIException, IOException, InvalidIdException {
        List<Anime> season = JikanAPI.getSeason(Season.FALL, 2023);

        season.sort(Comparator.comparingDouble(Anime::getScore));
        Collections.reverse(season);

        int counter = 0;

        for (Anime anime : season) {
            System.out.println(anime.getScore() + " " + anime.getTitle());
            saveAnimePicture(anime, counter);
            counter++;
        }
    }

    private static void saveAnimePicture(Anime anime, int counter) throws IOException {
        String name = checkWindowsCompatibleName(anime.getTitle());

        BufferedImage image = ImageIO.read(new URL(anime.getImages().getJpg().getLargeImage()));
        File file = new File("C:\\Users\\adria\\Desktop\\images\\winter2023\\" + (counter + 1) + " - " + name);
        new File(file.getParent()).mkdirs();
        ImageIO.write(image, "png", file);
    }

    private static String checkWindowsCompatibleName(@NotNull String fileName) {
        String invalidCharsRegex = "[/\\\\:*?\"<>|]";
        String invalidEndRegex = "[ .]+$";

        String windowsCompatibleName = fileName.replaceAll(invalidCharsRegex, "_");
        windowsCompatibleName = windowsCompatibleName.replaceAll(invalidEndRegex, "_");


        if (windowsCompatibleName.matches("^(?i)(con|prn|aux|nul|com[1-9]|lpt[1-9])\\.png$")) {
            windowsCompatibleName = "_" + windowsCompatibleName;
        }

        // Shouldn't exceed 255 characters (Windows file name limit)
        int maxFileNameLength = 255 - ".png".length();
        if (windowsCompatibleName.length() > maxFileNameLength) {
            windowsCompatibleName = windowsCompatibleName.substring(0, maxFileNameLength);
        }

        return windowsCompatibleName + ".png";
    }
}