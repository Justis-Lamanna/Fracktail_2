package com.github.lucbui.bot.config;

import com.github.lucbui.bot.services.translate.TranslateInvokerFactory;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.command.func.invoke.InvokerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TranslateConfig {
    @Bean
    public InvokerFactory invokerFactory(TranslateService translateService) {
        return new TranslateInvokerFactory(translateService);
    }
}
