package dev.zawarudo.holo.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class Formatter {

    private Formatter() {
    }

    public static String formatTrackTime(long timeInMillis) {
        long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
        long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatTime(long timeInMillis) {
        long days = timeInMillis / TimeUnit.DAYS.toMillis(1);
        long hours = timeInMillis % TimeUnit.DAYS.toMillis(1) / TimeUnit.HOURS.toMillis(1);
        long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
        long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        StringBuilder formatted = new StringBuilder(100);
        if (days > 0) {
            formatted.append(days).append(" days, ");
        }
        if (hours > 0) {
            formatted.append(hours).append(" hours, ");
        }
        if (minutes > 0) {
            formatted.append(minutes).append(" minutes and ");
        }
        return formatted.append(seconds).append(" seconds").toString();
    }

    /**
     * Turns a given amount of milliseconds to a date and time.
     */
    public static String formatDateTime(long millis) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);
        return dateFormat.format(new Date(millis));
    }

    /**
     * Returns the same String but with the first letter in uppercase.
     */
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase(Locale.UK) + string.substring(1);
    }

    /**
     * Encodes a given URL string to avoid unsafe characters.
     *
     * @param link The URL string to encode.
     * @return The encoded URL string.
     */
    public static String encodeUrl(String link) {
        return URLEncoder.encode(link, StandardCharsets.UTF_8);
    }
}