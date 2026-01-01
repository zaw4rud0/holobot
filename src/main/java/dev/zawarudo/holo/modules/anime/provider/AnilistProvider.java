package dev.zawarudo.holo.modules.anime.provider;

import dev.zawarudo.holo.modules.anime.MediaPlatform;
import dev.zawarudo.holo.modules.anime.model.AnimeResult;
import dev.zawarudo.holo.modules.anime.model.MangaResult;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;

import java.util.List;

public final class AnilistProvider implements MediaSearchProvider {

    @Override
    public MediaPlatform platform() {
        return MediaPlatform.ANILIST;
    }

    @Override
    public List<AnimeResult> searchAnime(String query, int limit) throws APIException, InvalidRequestException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public List<MangaResult> searchManga(String query, int limit) throws APIException, InvalidRequestException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
