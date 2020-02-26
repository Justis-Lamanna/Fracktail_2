package com.github.lucbui.calendarfun.config;

import com.github.lucbui.calendarfun.validation.CommandValidator;
import com.github.lucbui.calendarfun.validation.MessageValidator;
import discord4j.core.object.entity.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {
    @ConditionalOnMissingBean
    @Bean
    public MessageValidator messageValidator() {
        return event -> !event.getMessage().getAuthor().map(User::isBot).orElse(true);
    }

    @ConditionalOnMissingBean
    @Bean
    public CommandValidator commandValidator() {
        return (event, command) -> true;
    }
}
