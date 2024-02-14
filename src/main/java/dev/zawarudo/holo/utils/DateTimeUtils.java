package dev.zawarudo.holo.utils;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static long parseDateTime(@NotNull String input) {
        input = input.trim();

        Pattern pattern = Pattern.compile("\\(UTC([+-]\\d+)\\)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String sign = matcher.group(1).startsWith("+") ? "+" : "-";
            String hours = matcher.group(1).substring(1);
            hours = hours.length() == 1 ? "0" + hours : hours;
            String replacement = sign + hours + "00";
            input = matcher.replaceAll(replacement);
        }

        DateTimeFormatter[] dateTimeFormatters = new DateTimeFormatter[]{
                // European date time formats
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
                DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"),
                DateTimeFormatter.ofPattern("dd. MMMM yyyy HH:mm"),

                // ISO 8601
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),

                // American
                DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm"),

                // With timezone
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm Z"),
                DateTimeFormatter.ofPattern("dd/MM/yy HH:mm Z"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm Z"),
                DateTimeFormatter.ofPattern("dd.MM.yy HH:mm Z"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm Z"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm Z"),
        };

        DateTimeFormatter[] dateFormatters = new DateTimeFormatter[]{
                // TODO: Add support for entering just a year or month

                // European date time formats
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yy"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd.MM.yy"),

                // ISO 8601
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),

                // American
                DateTimeFormatter.ofPattern("MMMM d, yyyy"),
        };

        for (DateTimeFormatter formatter : dateTimeFormatters) {
            try {
                if (formatter.toString().contains("Offset")) {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(input, formatter);
                    return zonedDateTime.toInstant().toEpochMilli();
                } else {
                    LocalDateTime localDateTime = LocalDateTime.parse(input, formatter);
                    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                    return zonedDateTime.toInstant().toEpochMilli();
                }
            } catch (DateTimeParseException ignored) {
            }
        }

        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                LocalDate localDate = LocalDate.parse(input, formatter);
                ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
                return zonedDateTime.toInstant().toEpochMilli();
            } catch (DateTimeParseException ignored) {
            }
        }

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unsupported date time format: " + input);
        }
    }

    public static String getRelativeTime(long millis) {
        long diff = millis - System.currentTimeMillis();

        diff = Math.abs(diff);

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        diff -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        diff -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        diff -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" day").append(days > 1 ? "s" : "");
            if (hours > 0 || minutes > 0 || seconds > 0) sb.append(", ");
        }
        if (hours > 0) {
            sb.append(hours).append(" hour").append(hours > 1 ? "s" : "");
            if (minutes > 0 || seconds > 0) sb.append(", ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
            if (seconds > 0) sb.append(", ");
        }
        if (seconds > 0) {
            sb.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
        }

        if (sb.isEmpty()) {
            sb.append("less than a second");
        }

        return Formatter.capitalize(sb.toString());
    }

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
}