package com.github.lucbui.bot.services.translate;

import java.time.*;
import java.util.Date;

public class TranslateHelper {
    public static final String VALIDATION_PREFIX = "validation";
    public static final String LOW = VALIDATION_PREFIX + ".low";
    public static final String HIGH = VALIDATION_PREFIX + ".high";
    public static final String RANGE = VALIDATION_PREFIX + ".range";
    public static final String MONTH = VALIDATION_PREFIX + ".month";
    public static final String UNKNOWN_USER_OR_GUILD = VALIDATION_PREFIX + ".unknownUserOrGuild";

    public static String helpKey(String cmd) {
        return cmd + ".help";
    }

    public static String usageKey(String cmd) {
        return cmd + ".usage";
    }

    public static Date toDate(Month month) {
        return toDate(Year.now().atMonth(month).atDay(1));
    }

    public static Date toDate(MonthDay monthDay) {
        return toDate(Year.now().atMonthDay(monthDay));
    }

    public static Date toDate(LocalDate localDate) {
        return toDate(localDate.atStartOfDay());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return toDate(localDateTime.atZone(ZoneId.systemDefault()));
    }

    public static Date toDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date toDate(Instant instant) {
        return Date.from(instant);
    }
}
