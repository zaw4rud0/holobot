package utils;

import dev.zawarudo.holo.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeTests {

    @Test
    void testDateTimeFormat1() {
        String input = "February 26, 2024 23:59 (UTC+8)";

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2024, 2, 26, 23, 59, 0, 0, ZoneId.of("UTC+8"));
        long expected = zonedDateTime.toInstant().toEpochMilli();

        assertEquals(expected, DateTimeUtils.convertToMillis(input));
    }
}