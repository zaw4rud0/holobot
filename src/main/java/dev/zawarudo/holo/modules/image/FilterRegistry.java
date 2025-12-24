package dev.zawarudo.holo.modules.image;

import java.util.*;

public final class FilterRegistry {

    private static final Map<String, ImageFilter> FILTERS = new LinkedHashMap<>();

    static {
        register(new AcheronFilter());
    }

    private FilterRegistry() {
        throw new UnsupportedOperationException();
    }

    public static void register(ImageFilter filter) {
        FILTERS.put(filter.name().toLowerCase(Locale.ROOT), filter);
    }

    public static Optional<ImageFilter> get(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(FILTERS.get(name.toLowerCase(Locale.ROOT)));
    }

    public static List<ImageFilter> list() {
        return new ArrayList<>(new LinkedHashSet<>(FILTERS.values()));
    }
}