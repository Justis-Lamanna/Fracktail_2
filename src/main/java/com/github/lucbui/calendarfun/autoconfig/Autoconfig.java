package com.github.lucbui.calendarfun.autoconfig;

import com.github.lucbui.calendarfun.validation.UserCommandValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Autoconfig {
    @Bean
    @ConditionalOnMissingBean
    public UserCommandValidator userCommandValidator() {
        return (user, cmd) -> true;
    }
}
