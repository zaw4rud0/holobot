package dev.zawarudo.holo.modules.anime;

import dev.zawarudo.holo.modules.jikan.model.Anime;
import dev.zawarudo.holo.modules.jikan.model.Broadcast;

import java.util.List;

public class AnimeWeek {

    public static void sortAnimeByRelease(List<Anime> anime) {
        anime.sort((a1, a2) -> {
            String start1 = a1.getStartDate();
            String start2 = a2.getStartDate();

            int startComparison = start1.compareTo(start2);
            if (startComparison != 0) {
                return startComparison;
            }

            Broadcast b1 = a1.getBroadcast();
            Broadcast b2 = a2.getBroadcast();

            return b1.getTime().orElse("00:00").compareTo(b2.getTime().orElse("00:00"));
        });
    }
}
