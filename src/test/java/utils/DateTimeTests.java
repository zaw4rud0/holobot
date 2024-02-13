package utils;

import dev.zawarudo.holo.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeTests {

    @Test
    void testAmericanFormatWithTimezone() {
        String input = "February 26, 2024 23:59 (UTC+8)";

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2024, 2, 26, 23, 59, 0, 0, ZoneId.of("UTC+8"));
        long expected = zonedDateTime.toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.parseDateTime(input));
    }

    @Test
    void testAmericanFormat() {
        String input = "February 26, 2024 23:59";

        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 26, 23, 59);
        long expected = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.parseDateTime(input));
    }

    @Test
    void testAmericanFormatWithoutTime() {
        String input = "February 26, 2024";

        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 26, 0, 0);
        long expected = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.parseDateTime(input));
    }

    @Test
    void testEuropeanFormat() {
        String input = "26. February 2024 23:59";

        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 26, 23, 59);
        long expected = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.parseDateTime(input));
    }

    @Test
    void testMillis() {
        String input = "1708988340000";

        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 26, 23, 59);
        long expected = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.parseDateTime(input));
    }

    @Test
    void testEuropean() {
        List<String> inputs = List.of(
                "14.02.24 18:00",
                "14.02.24"
        );

        for (String input : inputs) {
            assertDoesNotThrow(() -> DateTimeUtils.parseDateTime(input));
        }
    }
}