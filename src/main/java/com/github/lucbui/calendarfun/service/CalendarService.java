package com.github.lucbui.calendarfun.service;

import com.github.lucbui.calendarfun.model.Birthday;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CalendarService {
    List<Birthday> getNextNBirthdays(int n) throws IOException;
    List<Birthday> getTodaysBirthday() throws IOException;
    Optional<Birthday> searchBirthday(String user) throws IOException;
    void addBirthday(Birthday birthday) throws IOException;
}
