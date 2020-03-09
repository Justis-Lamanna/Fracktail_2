package com.github.lucbui.bot;

import com.github.lucbui.magic.schedule.SchedulerService;
import com.github.lucbui.magic.schedule.cron.Cron;
import discord4j.core.DiscordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BotStartupRunner implements CommandLineRunner {
    @Autowired
    private DiscordClient bot;

    @Override
    public void run(String... args) throws Exception {
        bot.login().block();
    }

    private void printTheDateAndTime() {
        String now = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
        System.out.println("It is now: " + now);
    }
}
