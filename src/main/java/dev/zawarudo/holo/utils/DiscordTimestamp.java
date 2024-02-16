package dev.zawarudo.holo.utils;

/**
 * Creates a Discord timestamp with the given milliseconds.
 * <p>
 * For more information, refer to the <a href=https://discord.com/developers/docs/reference#message-formatting-timestamp-styles>official Discord docs</a>.
 */
public enum DiscordTimestamp {

    /**
     * Example: 13 February 2024 21:57
     */
    DEFAULT("<t:%d>"),
    /**
     * Example: 21:57
     */
    SHORT_TIME("<t:%d:t>"),
    /**
     * Example: 21:57:00
     */
    LONG_TIME("<t:%d:T>"),
    /**
     * Example: 13/02/24
     */
    SHORT_DATE("<t:%d:d>"),
    /**
     * Example: 13 February 2024
     */
    LONG_DATE("<t:%d:D>"),
    /**
     * Example: 13 February 2024 21:57
     */
    SHORT_DATE_TIME("<t:%d:f>"),
    /**
     * Example: Tuesday, 13 February 2024 21:57
     */
    LONG_DATE_TIME("<t:%d:F>"),
    /**
     * Example: 2 years ago
     */
    RELATIVE_TIME("<t:%d:R>");

    private final String format;

    DiscordTimestamp(String format) {
        this.format = format;
    }

    public String getTimestamp(long millis) {
        return String.format(format, millis / 1000L);
    }
}