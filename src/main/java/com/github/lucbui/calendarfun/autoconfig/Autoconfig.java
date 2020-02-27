package com.github.lucbui.calendarfun.autoconfig;

import com.github.lucbui.calendarfun.validation.command.CommandValidator;
import com.github.lucbui.calendarfun.validation.message.MessageValidator;
import com.github.lucbui.calendarfun.validation.user.UserValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Autoconfig {
    @Bean
    @ConditionalOnMissingBean
    public UserValidator userValidator() {
        return (user, cmd) -> true;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandValidator commandValidator() {
        return (event, command) -> true;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageValidator messageValidator(){
        return event -> true;
    }
}
