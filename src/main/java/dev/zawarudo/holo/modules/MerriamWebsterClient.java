package dev.zawarudo.holo.modules;

import com.google.gson.JsonArray;
import dev.zawarudo.holo.utils.HoloHttp;
import dev.zawarudo.holo.utils.exceptions.HttpStatusException;
import dev.zawarudo.holo.utils.exceptions.HttpTransportException;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public final class MerriamWebsterClient {

    public enum Product {
        DICTIONARY("collegiate"),
        THESAURUS("thesaurus");

        private final String path;

        Product(String path) {
            this.path = path;
        }
    }

    private static final String BASE = "https://www.dictionaryapi.com/api/v3/references/";

    private final String dictionaryKey;
    private final String thesaurusKey;

    public MerriamWebsterClient(String dictionaryKey, String thesaurusKey) {
        this.dictionaryKey = Objects.requireNonNull(dictionaryKey, "dictionaryKey must not be null");
        this.thesaurusKey = Objects.requireNonNull(thesaurusKey, "thesaurusKey must not be null");
    }

    public LookupResult lookupDictionary(@NotNull String term) {
        return lookup(Product.DICTIONARY, term, dictionaryKey);
    }

    public LookupResult lookupThesaurus(@NotNull String term) {
        return lookup(Product.THESAURUS, term, thesaurusKey);
    }

    public record LookupResult(List<Entry> entries, List<String> suggestions) {
        public LookupResult {
            entries = entries == null ? List.of() : List.copyOf(entries);
            suggestions = suggestions == null ? List.of() : List.copyOf(suggestions);
        }

        public boolean hasEntries() {
            return !entries.isEmpty();
        }

        public boolean hasSuggestions() {
            return !suggestions.isEmpty();
        }
    }

    public record Entry(
            String id,
            String headword,
            String functionalLabel,
            List<String> shortDefs,
            List<String> synonyms,
            List<String> antonyms
    ) {
    }

    private LookupResult lookup(Product product, @NotNull String term, String key) {
        String q = term.trim();
        if (q.isBlank()) {
            return new LookupResult(List.of(), List.of());
        }

        String url = BASE + product.path + "/json/" + encodePath(q) + "?key=" + encodeQuery(key);

        System.out.println(url);

        try {
            JsonArray array = HoloHttp.getJsonArray(url);

            System.out.println(array);

            return new LookupResult(List.of(), List.of());
        } catch (HttpStatusException | HttpTransportException ex) {
            // TODO: Refactor
            throw new RuntimeException("Dictionary request failed", ex);
        }
    }

    private static String encodePath(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String encodeQuery(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
