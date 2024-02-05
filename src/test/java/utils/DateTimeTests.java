package utils;

import dev.zawarudo.holo.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeTests {

    @Test
    void testAmericanFormatWithTimezone() {
        String input = "February 26, 2024 23:59 (UTC+8)";

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2024, 2, 26, 23, 59, 0, 0, ZoneId.of("UTC+8"));
        long expected = zonedDateTime.toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.convertToMillis(input));
    }

    @Test
    void testAmericanFormat() {
        String input = "February 26, 2024 23:59";

        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 26, 23, 59);
        long expected = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.convertToMillis(input));
    }

    @Test
    void testAmericanFormatWithoutTime() {
        String input = "February 26, 2024";

        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 26, 0, 0);
        long expected = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.convertToMillis(input));
    }

    @Test
    void testEuropeanFormat() {
        String input = "26. February 2024 23:59";

        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 26, 23, 59);
        long expected = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.convertToMillis(input));
    }
}