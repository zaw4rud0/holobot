package dev.zawarudo.holo.utils;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * The DateTimeUtils class provides utility methods for working with date and time operations. This class contains
 * methods to format date and time strings and various conversion methods to specific formats.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    /**
     * Formats the given timestamp in milliseconds into a localized date and time string.
     *
     * @param millis The timestamp in milliseconds to be formatted.
     * @return A string representation of the formatted date and time.
     */
    public static String formatDateTime(long millis) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);
        return dateFormat.format(new Date(millis));
    }

    /**
     * This method generates a string representation of the current local date and time,
     * formatted as "yyyy-MM-dd_HH.mm.ss". This format includes the year, month, day,
     * hours, minutes, and seconds, separated by underscores and periods for readability.
     * <p>
     * Example output: "2023-12-10_15.30.45" for December 10, 2023, at 3:30:45 PM.
     *
     * @return A string representing the current date and time in the format "yyyy-MM-dd_HH.mm.ss".
     */
    public static String getCurrentDateTimeString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
        return now.format(formatter);
    }

    public static long convertToMillis(String dateTime, String timeZoneId) {
        throw new UnsupportedOperationException();
    }

    public static long convertToMillis(String dateTime) {
        throw new UnsupportedOperationException();
    }
}