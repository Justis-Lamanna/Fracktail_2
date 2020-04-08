package com.github.lucbui.bot.services.calendar;

import com.github.lucbui.bot.model.Birthday;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Service
public class GoogleCalendarService implements CalendarService {
    private static final String BIRTHDAY_STR = "Birthday";

    @Value("${calendar.id}")
    private String calendarId;

    @Autowired
    private Calendar calendar;

    @Override
    public Flux<Birthday> getNextNBirthdays(int n) {
        LocalDate today = LocalDate.now();
        DateTime now = from(today.atStartOfDay().minus(1, ChronoUnit.MILLIS));
        //Cut off the search at one year from now, at which point birthdays will start to repeat.
        DateTime oneYear = from(today.atStartOfDay().plus(1, ChronoUnit.YEARS));
        return Mono.fromCallable(() ->
                calendar.events().list(calendarId)
                    .setMaxResults(n)
                    .setTimeMin(now)
                    .setTimeMax(oneYear)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .setQ(BIRTHDAY_STR)
                    .execute())
                .flatMapIterable(Events::getItems)
                .map(Birthday::new);
    }

    @Override
    public Flux<Birthday> getTodaysBirthday() {
        //Lists all birthday's on today
        LocalDate today = LocalDate.now();
        DateTime beginningOfToday = from(today.atStartOfDay().minus(1, ChronoUnit.MILLIS));
        DateTime endOfToday = from(today.atStartOfDay().plus(1, ChronoUnit.DAYS));
        return Mono.fromCallable(() ->
                calendar.events().list(calendarId)
                    .setMaxResults(25)
                    .setTimeMin(beginningOfToday)
                    .setTimeMax(endOfToday)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .setQ(BIRTHDAY_STR)
                    .execute())
                .flatMapIterable(Events::getItems)
                .map(Birthday::new);
    }

    @Override
    public Flux<Birthday> getMonthsBirthday(Month month) {
        DateTime startOfTheMonth = from(Year.now().atMonth(month).atDay(1).atStartOfDay().minus(1, ChronoUnit.MILLIS));
        DateTime endOfTheMonth = from(Year.now().atMonth(month).atEndOfMonth().atStartOfDay().plus(1, ChronoUnit.DAYS));
        return Mono.fromCallable(() ->
                calendar.events().list(calendarId)
                    .setMaxResults(25)
                    .setTimeMin(startOfTheMonth)
                    .setTimeMax(endOfTheMonth)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .setQ(BIRTHDAY_STR)
                    .execute())
                .flatMapIterable(Events::getItems)
                .map(Birthday::new);
    }

    @Override
    public Mono<Birthday> searchBirthday(String user) {
        DateTime beginningOfToday = from(LocalDate.now().atStartOfDay().minus(1, ChronoUnit.SECONDS));
        //Cut off the search at one year from now, at which point birthdays will start to repeat.
        DateTime oneYear = from(LocalDate.now().atStartOfDay().plus(1, ChronoUnit.YEARS));
        return Mono.fromCallable(() ->
                calendar.events().list(calendarId)
                    .setMaxResults(1)
                    .setTimeMin(beginningOfToday)
                    .setTimeMax(oneYear)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .setQ(user + "'s Birthday")
                    .execute())
                .flatMap(events -> events.isEmpty() ? Mono.empty() : Mono.just(events.getItems().get(0)))
                .map(Birthday::new);
    }

    @Override
    public Mono<Birthday> searchBirthdayById(Snowflake id) {
        DateTime beginningOfToday = from(LocalDate.now().atStartOfDay().minus(1, ChronoUnit.SECONDS));
        //Cut off the search at one year from now, at which point birthdays will start to repeat.
        DateTime oneYear = from(LocalDate.now().atStartOfDay().plus(1, ChronoUnit.YEARS));
        return Mono.fromCallable(() ->
                calendar.events().list(calendarId)
                    .setMaxResults(1)
                    .setTimeMin(beginningOfToday)
                    .setTimeMax(oneYear)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .setPrivateExtendedProperty(Collections.singletonList("discord_id=" + id.asString()))
                    .execute())
                .flatMap(events -> events.isEmpty() ? Mono.empty() : Mono.just(events.getItems().get(0)))
                .map(Birthday::new);
    }

    @Override
    public Mono<Void> addBirthday(Birthday birthday) {
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

        return Mono.fromCallable(() -> calendar.events().insert(calendarId, event).execute())
                .then();
    }

    private DateTime from(LocalDateTime time) {
        return new DateTime(time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
    }
}
