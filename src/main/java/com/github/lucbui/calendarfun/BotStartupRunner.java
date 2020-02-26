package com.github.lucbui.calendarfun;

import com.github.lucbui.calendarfun.service.CalendarService;
import discord4j.core.DiscordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BotStartupRunner implements CommandLineRunner {
    @Autowired
    private DiscordClient bot;

    @Override
    public void run(String... args) throws Exception {
        bot.login().block();
    }
}
