package dev.zawarudo.holo.modules.anime.model;

import dev.zawarudo.holo.modules.anime.MediaPlatform;

import java.util.List;

public record MangaResult(
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
        int chapters,
        int volumes,
        String status,
        List<String> authors,
        List<String> genres,
        List<String> themes,
        List<String> demographics
) {
}
