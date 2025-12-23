package dev.zawarudo.holo.utils.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generic HTTP exception.
 */
public final class HttpStatusException extends Exception {
    private final String url;
    private final int statusCode;
    private final String bodySnippet;

    public HttpStatusException(@NotNull String url, int statusCode, @Nullable String bodySnippet) {
        super("HTTP " + statusCode + " for " + url + (bodySnippet == null || bodySnippet.isBlank() ? "" : " - " + bodySnippet));
        this.url = url;
        this.statusCode = statusCode;
        this.bodySnippet = bodySnippet;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getUrl() {
        return url;
    }

    public String getBodySnippet() {
        return bodySnippet;
    }
}