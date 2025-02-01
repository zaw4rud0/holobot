package dev.zawarudo.holo.modules.anime;

import dev.zawarudo.holo.modules.jikan.JikanAPI;
import dev.zawarudo.holo.modules.jikan.model.Anime;
import dev.zawarudo.holo.modules.jikan.model.Season;
import dev.zawarudo.holo.utils.exceptions.APIException;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 */
public final class SeasonPlan {

    private SeasonPlan() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Creates an image displaying the anime that air on the respective week day.
     */
    public static BufferedImage createWeekPlan(Season season, int year) throws APIException {
        List<Anime> seasonalAnime = JikanAPI.getSeason(season, year);

        for (Anime anime : seasonalAnime) {
            anime.changeBroadcastTimeZone("Europe/Zurich");

            System.out.println(anime.getTitle());

            System.out.println(anime.getBroadcast().getDay());
            System.out.println(anime.getBroadcast().getTime());
        }

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Creates an image displaying the start dates of the seasonal anime.
     */
    public static BufferedImage createStartPlan(Season season, int year) throws APIException {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    public static void main(String[] args) throws APIException {
        createWeekPlan(Season.FALL, 2024);
    }
}