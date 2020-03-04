package com.github.lucbui.calendarfun.config;

import com.github.lucbui.calendarfun.command.CommandFieldCallbackFactory;
import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.command.store.*;
import com.github.lucbui.calendarfun.token.PrefixTokenizer;
import com.github.lucbui.calendarfun.token.Tokenizer;
import com.github.lucbui.calendarfun.validation.command.CommandValidator;
import com.github.lucbui.calendarfun.validation.message.MessageValidator;
import com.github.lucbui.calendarfun.validation.user.UserValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class AutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public CommandFieldCallbackFactory commandFieldCallbackFactory(CommandList commandList, Tokenizer tokenizer) {
        return new CommandFieldCallbackFactory(commandList, tokenizer);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageValidator messageValidator() {
        return event -> true;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandValidator commandValidator() {
        return (event, command) -> true;
    }

    @Bean
    @ConditionalOnMissingBean
    public UserValidator userValidator() {
        return (user, command) -> true;
    }

    @Bean
    @ConditionalOnProperty("discord.caseInsensitiveCommands")
    public CommandStoreMapFactory commandStoreMapFactory(@Value("${discord.caseInsensitiveCommands}") boolean caseInsensitive) {
        return new CommandStoreSelectableMapFactory(caseInsensitive);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandStoreMapFactory commandStoreMapFactory() {
        return HashMap::new;
    }

    @Bean
    @ConditionalOnProperty("discord.prefix")
    public Tokenizer tokenizer(@Value("${discord.prefix}") String prefix) {
        return new PrefixTokenizer(prefix);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandList commandList(CommandStoreMapFactory commandStoreMapFactory) {
        return new CommandList(commandStoreMapFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandHandler commandHandler(Tokenizer tokenizer, MessageValidator messageValidator, CommandValidator commandValidator,
                                         UserValidator userValidator, CommandList commandList) {
        return new DefaultCommandHandler(tokenizer, messageValidator, commandValidator, userValidator, commandList);
    }
}
