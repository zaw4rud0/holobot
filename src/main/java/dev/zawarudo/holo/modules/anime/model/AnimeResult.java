package dev.zawarudo.holo.modules.anime.model;

import dev.zawarudo.holo.modules.anime.MediaPlatform;

import java.util.List;

public record AnimeResult(
        MediaPlatform platform,
        int id,
        String title,
        String type,
        String url,
        String imageUrl,
        String synopsis,
        String titleEnglish,
        String titleJapanese,
        double score,
        int rank,
        int episodes,
        String status,
        String season,
        List<String> studios,
        List<String> genres,
        List<String> themes,
        List<String> demographics
) {
}
