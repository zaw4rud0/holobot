package dev.zawarudo.holo.modules.anime.provider;

import dev.zawarudo.holo.modules.anime.MediaPlatform;
import dev.zawarudo.holo.modules.anime.jikan.JikanApiClient;
import dev.zawarudo.holo.modules.anime.jikan.model.Anime;
import dev.zawarudo.holo.modules.anime.jikan.model.Manga;
import dev.zawarudo.holo.modules.anime.jikan.model.Nameable;
import dev.zawarudo.holo.modules.anime.model.AnimeResult;
import dev.zawarudo.holo.modules.anime.model.MangaResult;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class JikanProvider implements MediaSearchProvider {

    @Override
    public MediaPlatform platform() {
        return MediaPlatform.MAL_JIKAN;
    }

    @Override
    public List<AnimeResult> searchAnime(@NotNull String query, int limit) throws APIException, InvalidRequestException {
        JikanApiClient.setLimit(limit);

        List<Anime> results = JikanApiClient.searchAnime(query);
        return results.stream().map(JikanProvider::mapAnime).toList();
    }

    @Override
    public List<MangaResult> searchManga(@NotNull String query, int limit) throws APIException, InvalidRequestException {
        JikanApiClient.setLimit(limit);

        List<Manga> res = JikanApiClient.searchManga(query);
        return res.stream().map(JikanProvider::mapManga).toList();
    }

    private static AnimeResult mapAnime(Anime anime) {
        String title = anime.getTitle();

        String imageUrl = null;
        if (anime.getImages() != null && anime.getImages().getJpg() != null) {
            imageUrl = anime.getImages().getJpg().getLargeImage();
            if (imageUrl == null) imageUrl = anime.getImages().getJpg().getImage();
        }

        return new AnimeResult(
                MediaPlatform.MAL_JIKAN,
                anime.getId(),
                title,
                anime.getType(),
                anime.getUrl(),
                imageUrl,
                anime.getSynopsis().orElse(null),

                anime.getTitleEnglish().orElse(null),
                anime.getTitleJapanese().orElse(null),

                anime.getScore(),
                anime.getRank(),
                anime.getEpisodes(),
                anime.getStatus(),
                anime.getSeason() == null ? null : anime.getSeason(),
                anime.getStudios().stream().map(Nameable::getName).toList(),
                anime.getGenres().stream().map(Nameable::getName).toList(),
                anime.getThemes().stream().map(Nameable::getName).toList(),
                anime.getDemographics().stream().map(Nameable::getName).toList()
        );
    }

    private static MangaResult mapManga(Manga manga) {
        String title = manga.getTitle();

        String imageUrl = null;
        if (manga.getImages() != null && manga.getImages().getJpg() != null) {
            imageUrl = manga.getImages().getJpg().getLargeImage();
            if (imageUrl == null) imageUrl = manga.getImages().getJpg().getImage();
        }

        return new MangaResult(
                MediaPlatform.MAL_JIKAN,
                manga.getId(),
                title,
                manga.getType(),
                manga.getUrl(),
                imageUrl,
                manga.getSynopsis().orElse(null),

                manga.getTitleEnglish().orElse(null),
                manga.getTitleJapanese().orElse(null),

                manga.getScore(),
                manga.getRank(),
                manga.getChapters(),
                manga.getVolumes(),
                manga.getStatus(),

                formatAuthors(manga.getAuthors()),

                manga.getGenres().stream().map(Nameable::getName).toList(),
                manga.getThemes().stream().map(Nameable::getName).toList(),
                manga.getDemographics().stream().map(Nameable::getName).toList()
        );
    }

    private static List<String> formatAuthors(List<Nameable> list) {
        return list.stream().map(Nameable::getName).map(Formatter::reverseJapaneseName).toList();
    }
}
