package dev.zawarudo.holo.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public final class TypeTokenUtils {

    private TypeTokenUtils() {
    }

    /**
     * Creates a List of the given class.
     *
     * @param ignoredClazz The class to create a List of.
     * @return The list type.
     */
    public static <T> Type getListTypeToken(Class<T> ignoredClazz) {
        return TypeToken.getParameterized(List.class, ignoredClazz).getType();
    }
}