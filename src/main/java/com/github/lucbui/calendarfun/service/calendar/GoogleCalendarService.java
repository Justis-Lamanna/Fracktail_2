package com.github.lucbui.calendarfun.service.calendar;

import com.github.lucbui.calendarfun.model.Birthday;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GoogleCalendarService implements CalendarService {
    private static final String BIRTHDAY_STR = "Birthday";

    @Value("${calendar.id}")
    private String calendarId;

    @Autowired
    private Calendar calendar;

    @Override
    public List<Birthday> getNextNBirthdays(int n) throws IOException {
        DateTime now = from(LocalDate.now().atStartOfDay().minus(1, ChronoUnit.SECONDS));
        //Cut off the search at one year from now, at which point birthdays will start to repeat.
        DateTime oneYear = from(LocalDate.now().atStartOfDay().plus(1, ChronoUnit.YEARS));
        Events events = calendar.events().list(calendarId)
                .setMaxResults(n)
                .setTimeMin(now)
                .setTimeMax(oneYear)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setQ(BIRTHDAY_STR)
                .execute();
        return events.getItems().stream()
                .map(Birthday::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Birthday> getTodaysBirthday() throws IOException {
        //Lists all birthday's on today
        LocalDate today = LocalDate.now();
        DateTime beginningOfToday = from(today.atStartOfDay().minus(1, ChronoUnit.SECONDS));
        DateTime endOfToday = from(today.atStartOfDay().plus(1, ChronoUnit.DAYS));
        Events events = calendar.events().list(calendarId)
                .setMaxResults(1)
                .setTimeMin(beginningOfToday)
                .setTimeMax(endOfToday)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setQ(BIRTHDAY_STR)
                .execute();
        return events.getItems().stream()
                .map(Birthday::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Birthday> searchBirthday(String user) throws IOException {
        DateTime beginningOfToday = from(LocalDate.now().atStartOfDay().minus(1, ChronoUnit.SECONDS));
        //Cut off the search at one year from now, at which point birthdays will start to repeat.
        DateTime oneYear = from(LocalDate.now().atStartOfDay().plus(1, ChronoUnit.YEARS));
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

    @Override
    public Optional<Birthday> searchBirthdayById(Snowflake id) throws IOException {
        DateTime beginningOfToday = from(LocalDate.now().atStartOfDay().minus(1, ChronoUnit.SECONDS));
        //Cut off the search at one year from now, at which point birthdays will start to repeat.
        DateTime oneYear = from(LocalDate.now().atStartOfDay().plus(1, ChronoUnit.YEARS));
        Events events = calendar.events().list(calendarId)
                .setMaxResults(1)
                .setTimeMin(beginningOfToday)
                .setTimeMax(oneYear)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setPrivateExtendedProperty(Collections.singletonList("discord_id=" + id.asString()))
                .execute();
        List<Event> eventList = events.getItems();
        if(eventList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Birthday(eventList.get(0)));
        }
    }

    @Override
    public void addBirthday(Birthday birthday) throws IOException {
        EventDateTime eventTime = new EventDateTime()
            .setDate(new DateTime(DateTimeFormatter.ISO_LOCAL_DATE.format(birthday.getDate())))
            .setTimeZone(ZoneId.systemDefault().getId());

        Event event = new Event()
            .setStart(eventTime)
            .setEnd(eventTime)
            .setSummary(birthday.getName() + "'s Birthday")
            .setRecurrence(Collections.singletonList("RRULE:FREQ=YEARLY"))
            .setTransparency("transparent")
            .setExtendedProperties(new Event.ExtendedProperties().setPrivate(Collections.singletonMap("discord_id", birthday.getMemberId())))
            .setVisibility("public");

        calendar.events().insert(calendarId, event).execute();
    }

    private DateTime from(LocalDateTime time) {
        return new DateTime(time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
    }
}
