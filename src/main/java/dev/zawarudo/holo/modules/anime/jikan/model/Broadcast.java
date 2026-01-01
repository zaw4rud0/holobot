package dev.zawarudo.holo.modules.anime.jikan.model;

import com.google.gson.annotations.SerializedName;
import dev.zawarudo.holo.utils.DateTimeUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public final class Broadcast {
    @SerializedName("day")
    String day;
    @SerializedName("time")
    String time;
    @SerializedName("timezone")
    String timeZone;
    @SerializedName("string")
    String string;

    /**
     * The weekday when episodes of this anime are or were being released.
     */
    public Optional<String> getDay() {
        return Optional.ofNullable(day);
    }

    /**
     * The time (24-hour clock) when episodes of this anime are or were being released. Make
     * sure to check the time zone using the {@link #getTimeZone()} method as well.
     */
    public Optional<String> getTime() {
        return Optional.ofNullable(time);
    }

    /**
     * The time zone in which the weekday and time are given. Usually JST or Asia/Tokyo.
     */
    public Optional<String> getTimeZone() {
        return Optional.ofNullable(timeZone);
    }

    /**
     * The day and time as a formatted String. Includes the time zone in parentheses.
     */
    public Optional<String> getString() {
        if ("Unknown".equals(string)) {
            return Optional.empty();
        }
        return Optional.ofNullable(string);
    }

    /**
     * Creates a new Broadcast object with the adjusted fields for the given time zone ID.
     */
    Broadcast convertToTimeZone(String timeZone) {
        DateTimeUtils.checkTimeZone(timeZone);

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        ZoneId originalZoneId = ZoneId.of(this.timeZone);
        ZoneId targetZoneId = ZoneId.of(timeZone);

        LocalTime localTime = LocalTime.parse(this.time, timeFormatter);
        String singularDay = getSingularDay(this.day);
        DayOfWeek dayOfWeek = DayOfWeek.from(dayFormatter.parse(singularDay));
        LocalDate date = LocalDate.now(originalZoneId).with(TemporalAdjusters.nextOrSame(dayOfWeek));
        LocalDateTime localDateTime = LocalDateTime.of(date, localTime);
        ZonedDateTime originalZonedDateTime = ZonedDateTime.of(localDateTime, originalZoneId);
        ZonedDateTime targetZonedDateTime = originalZonedDateTime.withZoneSameInstant(targetZoneId);

        String convertedDay = getPluralDay(targetZonedDateTime.format(dayFormatter));
        String convertedTime = targetZonedDateTime.format(timeFormatter);

        Broadcast newBroadcast = new Broadcast();
        newBroadcast.day = convertedDay;
        newBroadcast.time = convertedTime;
        newBroadcast.timeZone = timeZone;
        newBroadcast.string = String.format("%s at %s (%s)", convertedDay, convertedTime, timeZone);

        return newBroadcast;
    }

    private static String getSingularDay(String day) {
        if (day.endsWith("s")) {
            return day.substring(0, day.length() - 1);
        }
        return day;
    }

    private static String getPluralDay(String day) {
        if (!day.endsWith("s")) {
            return day + "s";
        }
        return day;
    }
}