package com.github.lucbui.bot;

import discord4j.core.DiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BotStartupRunner implements CommandLineRunner, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotStartupRunner.class);

    private ApplicationContext context;

    @Autowired
    private DiscordClient bot;

    @Override
    public void run(String... args) throws Exception {
        bot.login().block();
        LOGGER.info("Bot has turned off");

        SpringApplication.exit(context, () -> 0);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
