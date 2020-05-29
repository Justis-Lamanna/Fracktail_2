package com.github.lucbui.bot.schedule;

import java.time.MonthDay;

public interface Event {
    String getName();
    MonthDay getMonthDay();
}
