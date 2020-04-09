package com.github.lucbui.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ResourceBundle;

@Configuration
public class LanguageConfig {
    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("fracktail");
    }
}
