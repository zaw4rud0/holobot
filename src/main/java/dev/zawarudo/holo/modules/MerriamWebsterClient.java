package dev.zawarudo.holo.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.HoloHttp;
import dev.zawarudo.holo.utils.exceptions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <a href="https://dictionaryapi.com/products/json">Documentation of the JSON structure</a>
 */
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

    public LookupResult lookupDictionary(@NotNull String term) throws APIException, NotFoundException, InvalidRequestException {
        return lookup(Product.DICTIONARY, term, dictionaryKey);
    }

    public LookupResult lookupThesaurus(@NotNull String term) throws APIException, NotFoundException, InvalidRequestException {
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
            List<String> antonyms,
            List<String> examples,
            String etymology,
            boolean offensive
    ) {
        public Entry {
            headword = headword.replace("*", "·");
            shortDefs = shortDefs == null ? List.of() : List.copyOf(shortDefs);
            synonyms = synonyms == null ? List.of() : List.copyOf(synonyms);
            antonyms = antonyms == null ? List.of() : List.copyOf(antonyms);
            examples = examples == null ? List.of() : List.copyOf(examples);
        }
    }

    private LookupResult lookup(Product product, @NotNull String term, String key) throws APIException, NotFoundException, InvalidRequestException {
        String q = term.trim();
        if (q.isBlank()) {
            return new LookupResult(List.of(), List.of());
        }

        String url = BASE + product.path + "/json/" + encodePath(q) + "?key=" + encodeQuery(key);

        final JsonArray array;
        try {
            array = HoloHttp.getJsonArray(url);
        } catch (HttpStatusException ex) {
            mapHttpStatus(product, ex);
            throw new APIException("Unreachable", ex);
        } catch (HttpTransportException ex) {
            throw new APIException("Transport error while contacting Merriam-Webster (" + product.name().toLowerCase() + ").", ex);
        } catch (RuntimeException ex) {
            throw new InvalidRequestException("Unexpected response from Merriam-Webster (" + product.name().toLowerCase() + "). " + "Check your API key configuration.", ex);
        }

        if (array.isEmpty()) {
            return new LookupResult(List.of(), List.of());
        }

        JsonElement first = array.get(0);
        if (first.isJsonPrimitive()) {
            return new LookupResult(List.of(), parseSuggestions(array));
        }

        return new LookupResult(parseEntries(product, array), List.of());
    }

    private static void mapHttpStatus(Product product, HttpStatusException ex)
            throws APIException, InvalidRequestException, NotFoundException {

        int code = ex.getStatusCode();
        String name = product.name().toLowerCase();

        if (code == 404) {
            throw new NotFoundException("Merriam-Webster endpoint not found (" + name + ").", ex);
        }
        if (code == 429) {
            throw new APIException("429 Too Many Requests (Merriam-Webster " + name + " rate limit).", ex);
        }
        if (code >= 400 && code < 500) {
            throw new InvalidRequestException(code + " Client error from Merriam-Webster (" + name + ").", ex);
        }
        throw new APIException(code + " Server error from Merriam-Webster (" + name + ").", ex);
    }

    private static List<String> parseSuggestions(JsonArray array) {
        List<String> out = new ArrayList<>(Math.min(array.size(), 25));
        for (JsonElement el : array) {
            if (el != null && el.isJsonPrimitive()) {
                String s = safeString(el);
                if (!s.isBlank()) out.add(s);
            }
        }
        return out;
    }

    private static List<Entry> parseEntries(Product product, JsonArray array) {
        List<Entry> out = new ArrayList<>(Math.min(array.size(), 25));

        for (JsonElement el : array) {
            if (el == null || !el.isJsonObject()) continue;

            JsonObject obj = el.getAsJsonObject();

            JsonObject meta = obj.has("meta") && obj.get("meta").isJsonObject() ? obj.getAsJsonObject("meta") : null;
            JsonObject hwi = obj.has("hwi") && obj.get("hwi").isJsonObject() ? obj.getAsJsonObject("hwi") : null;

            String id = meta == null ? null : safeString(meta.get("id"));
            String headword = hwi == null ? null : safeString(hwi.get("hw"));
            String fl = safeString(obj.get("fl"));

            List<String> shortDefs = obj.has("shortdef") && obj.get("shortdef").isJsonArray()
                    ? stringList(obj.getAsJsonArray("shortdef"))
                    : List.of();

            List<String> examples = extractExamples(obj);

            List<String> syns = List.of();
            List<String> ants = List.of();
            if (product == Product.THESAURUS && meta != null) {
                syns = flattenNestedStringLists(meta.get("syns"));
                ants = flattenNestedStringLists(meta.get("ants"));
            }

            String etymology = extractEtymology(obj);

            boolean offensive = meta != null
                    && meta.has("offensive")
                    && meta.get("offensive").isJsonPrimitive()
                    && meta.get("offensive").getAsBoolean();

            out.add(new Entry(
                    id,
                    headword,
                    fl,
                    shortDefs,
                    syns,
                    ants,
                    examples,
                    etymology,
                    offensive
            ));
        }

        return out;
    }

    private static List<String> extractExamples(JsonObject obj) {
        if (!obj.has("def") || !obj.get("def").isJsonArray()) return List.of();

        List<String> out = new ArrayList<>();

        JsonArray defArr = obj.getAsJsonArray("def");
        for (JsonElement defEl : defArr) {
            if (defEl == null || !defEl.isJsonObject()) continue;
            JsonObject defObj = defEl.getAsJsonObject();

            if (!defObj.has("sseq") || !defObj.get("sseq").isJsonArray()) continue;
            JsonArray sseq = defObj.getAsJsonArray("sseq");

            // sseq is deeply nested arrays; walk it defensively
            walkJson(sseq, el -> {
                if (!el.isJsonArray()) return;

                JsonArray arr = el.getAsJsonArray();
                if (arr.size() < 2) return;

                // In many shapes, [ "sense", { ... } ] appears
                JsonElement maybeSenseObj = arr.get(1);
                if (!maybeSenseObj.isJsonObject()) return;

                JsonObject sense = maybeSenseObj.getAsJsonObject();
                if (!sense.has("dt") || !sense.get("dt").isJsonArray()) return;

                JsonArray dt = sense.getAsJsonArray("dt");
                for (JsonElement dtEl : dt) {
                    if (dtEl == null || !dtEl.isJsonArray()) continue;
                    JsonArray dtPair = dtEl.getAsJsonArray();
                    if (dtPair.size() < 2) continue;

                    String tag = safeString(dtPair.get(0));
                    if (!"vis".equals(tag)) continue;

                    JsonElement visEl = dtPair.get(1);
                    if (!visEl.isJsonArray()) continue;

                    for (JsonElement v : visEl.getAsJsonArray()) {
                        if (v != null && v.isJsonObject()) {
                            String t = Formatter.mwToDiscord(safeString(v.getAsJsonObject().get("t")));
                            if (!t.isBlank()) out.add(t);
                        }
                    }
                }
            });

        }

        // de-dupe but preserve order, cap a bit
        if (out.isEmpty()) return List.of();

        LinkedHashSet<String> dedup = new LinkedHashSet<>(out);
        List<String> result = new ArrayList<>(dedup);
        return result.size() > 20 ? result.subList(0, 20) : result;
    }

    private static void walkJson(JsonElement root, java.util.function.Consumer<JsonElement> fn) {
        if (root == null) return;
        fn.accept(root);

        if (root.isJsonArray()) {
            for (JsonElement el : root.getAsJsonArray()) {
                walkJson(el, fn);
            }
        } else if (root.isJsonObject()) {
            for (Map.Entry<String, JsonElement> e : root.getAsJsonObject().entrySet()) {
                walkJson(e.getValue(), fn);
            }
        }
    }

    private static List<String> stringList(JsonArray arr) {
        List<String> out = new ArrayList<>(arr.size());
        for (JsonElement el : arr) {
            String s = safeString(el);
            if (!s.isBlank()) out.add(s);
        }
        return out;
    }

    private static List<String> flattenNestedStringLists(@Nullable JsonElement el) {
        if (el == null || !el.isJsonArray()) return List.of();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (JsonElement outer : el.getAsJsonArray()) {
            if (outer != null && outer.isJsonArray()) {
                for (JsonElement inner : outer.getAsJsonArray()) {
                    String s = safeString(inner);
                    if (!s.isBlank()) out.add(s);
                }
            }
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    private static String safeString(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) return "";
        if (el.isJsonPrimitive()) return el.getAsString();
        return "";
    }

    private static String encodePath(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String encodeQuery(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static @Nullable String extractEtymology(JsonObject obj) {
        if (!obj.has("et") || !obj.get("et").isJsonArray()) return null;

        List<String> parts = new ArrayList<>();
        JsonArray etArr = obj.getAsJsonArray("et");

        for (JsonElement etEl : etArr) {
            if (etEl == null || !etEl.isJsonArray()) continue;

            JsonArray pair = etEl.getAsJsonArray();
            if (pair.size() < 2) continue;

            String tag = safeString(pair.get(0));
            if (!"text".equals(tag)) continue;

            String text = Formatter.mwToDiscord(safeString(pair.get(1)));
            if (!text.isBlank()) parts.add(text);
        }

        if (parts.isEmpty()) return null;

        String joined = String.join(" ", parts).trim();
        return joined.isBlank() ? null : joined;
    }
}
