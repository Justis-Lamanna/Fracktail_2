package com.github.lucbui.calendarfun.service;

import com.github.lucbui.calendarfun.model.Birthday;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BasicCalendarServiceImpl implements CalendarService {

    @Value("${calendar.id}")
    private String calendarId;

    @Autowired
    private Calendar calendar;

    @Override
    public List<Birthday> getNextNBirthdays(int n) throws IOException {
        // List the next n events from the birthday calendar.
        LocalDate today = LocalDate.now();
        DateTime now = new DateTime(today.atStartOfDay().minus(1, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        Events events = calendar.events().list(calendarId)
                .setMaxResults(n)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems().stream()
                .map(Birthday::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Birthday> getTodaysBirthday() throws IOException {
        //Lists all birthday's on today
        LocalDate today = LocalDate.now();
        DateTime beginningOfToday = new DateTime(today.atStartOfDay().minus(1, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        DateTime endOfToday = new DateTime(today.atStartOfDay().plus(1, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        Events events = calendar.events().list(calendarId)
                .setMaxResults(1)
                .setTimeMin(beginningOfToday)
                .setTimeMax(endOfToday)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems().stream()
                .map(Birthday::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Birthday> searchBirthday(String user) throws IOException {
        DateTime beginningOfToday = new DateTime(LocalDate.now().atStartOfDay().minus(1, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        DateTime oneYear = new DateTime(LocalDate.now().atStartOfDay().plus(1, ChronoUnit.YEARS).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        Events events = calendar.events().list(calendarId)
                .setMaxResults(1)
                .setTimeMin(beginningOfToday)
                .setTimeMax(oneYear)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setQ(user + "'s Birthday")
                .execute();
        List<Event> eventList = events.getItems();
        if(eventList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Birthday(eventList.get(0)));
        }
    }
}
