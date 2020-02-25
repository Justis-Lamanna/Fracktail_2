package com.github.lucbui.calendarfun.service;

import com.github.lucbui.calendarfun.model.Birthday;

import java.io.IOException;
import java.util.List;

public interface CalendarService {
    List<Birthday> getNextNBirthdays(int n) throws IOException;
    List<Birthday> getTodaysBirthday() throws IOException;
}
