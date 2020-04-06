package com.github.lucbui.bot.config;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ScheduleConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);
    public static final Snowflake LUCBUI_SNOWFLAKE = Snowflake.of("248612704019808258");

    @Bean
    public TaskScheduler taskScheduler(DiscordClient bot) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setErrorHandler(throwable -> {
            LOGGER.error("Error running job", throwable);
            bot.getUserById(LUCBUI_SNOWFLAKE)
                    .flatMap(User::getPrivateChannel)
                    .flatMap(dm -> dm.createMessage("There was an error running a job: " + throwable.getMessage() + " Please check the logs."))
                    .block();
        });
        return scheduler;
    }
}
