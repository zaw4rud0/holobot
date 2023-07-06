package dev.zawarudo.holo.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public final class TypeTokenUtils {

    private TypeTokenUtils() {
    }

    public static <T> Type getListTypeToken(Class<T> ignoredClazz) {
        return new TypeToken<List<T>>() {}.getType();
    }
}