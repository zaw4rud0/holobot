package dev.zawarudo.holo.utils;

import dev.zawarudo.holo.core.Bootstrap;

public final class VersionInfo {
    private VersionInfo() {
        throw new UnsupportedOperationException();
    }

    public static String getVersion() {
        Package p = Bootstrap.class.getPackage();
        String v = (p != null) ? p.getImplementationVersion() : null;
        return (v == null || v.isBlank()) ? "dev" : v;
    }
}