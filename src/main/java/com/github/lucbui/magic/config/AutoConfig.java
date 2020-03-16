package com.github.lucbui.magic.config;

import com.github.lucbui.magic.command.CommandAnnotationProcessor;
import com.github.lucbui.magic.command.CommandFieldCallbackFactory;
import com.github.lucbui.magic.command.store.*;
import com.github.lucbui.magic.token.PrefixTokenizer;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
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
    public CommandList commandList(CommandStoreMapFactory commandStoreMapFactory) {
        return new CommandList(commandStoreMapFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandAnnotationProcessor commandAnnotationProcessor(CommandFieldCallbackFactory commandFieldCallbackFactory) {
        return new CommandAnnotationProcessor(commandFieldCallbackFactory);
    }

    @Bean
    @ConditionalOnProperty("discord.commands.prefix")
    @ConditionalOnMissingBean
    public Tokenizer tokenizer(@Value("${discord.commands.prefix}") String prefix) {
        return new PrefixTokenizer(prefix);
    }

    @Bean
    @ConditionalOnMissingBean
    public CreateMessageValidator createMessageValidator() {
        return (event, command) -> true;
    }

    @Bean
    @ConditionalOnProperty("discord.commands.caseInsensitive")
    @ConditionalOnMissingBean
    public CommandStoreMapFactory commandStoreMapFactory(@Value("${discord.commands.caseInsensitive}") boolean caseInsensitive) {
        return new CommandStoreSelectableMapFactory(caseInsensitive);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandStoreMapFactory commandStoreMapFactory() {
        return HashMap::new;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandHandler commandHandler(Tokenizer tokenizer, CreateMessageValidator createMessageValidator, CommandList commandList) {
        return new DefaultCommandHandler(tokenizer, createMessageValidator, commandList);
    }
}
