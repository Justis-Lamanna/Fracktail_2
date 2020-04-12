package com.github.lucbui.bot.services.translate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TranslateHelper {
    public static final String VALIDATION_PREFIX = "validation";
    public static final String LOW = VALIDATION_PREFIX + ".low";
    public static final String HIGH = VALIDATION_PREFIX + ".high";
    public static final String RANGE = VALIDATION_PREFIX + ".range";
    public static final String MONTH = VALIDATION_PREFIX + ".month";

    public static String helpKey(String cmd) {
        return cmd + ".help";
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
