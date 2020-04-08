package com.github.lucbui.bot.services.calendar;

import com.github.lucbui.bot.model.Birthday;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface CalendarService {
    Flux<Birthday> getNextNBirthdays(int n);
    Flux<Birthday> getTodaysBirthday();
    Flux<Birthday> getMonthsBirthday(Month month);

    Mono<Birthday> searchBirthday(String user);
    Mono<Birthday> searchBirthdayById(Snowflake id);

    Mono<Void> addBirthday(Birthday birthday);
}
