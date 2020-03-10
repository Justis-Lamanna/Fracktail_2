package com.github.lucbui.magic.config;

import com.github.lucbui.magic.command.CommandFieldCallbackFactory;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.*;
import com.github.lucbui.magic.token.PrefixTokenizer;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.command.CommandValidator;
import com.github.lucbui.magic.validation.message.MessageValidator;
import com.github.lucbui.magic.validation.user.UserValidator;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
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
    @ConditionalOnProperty("discord.commands.prefix")
    @ConditionalOnMissingBean
    public Tokenizer tokenizer(@Value("${discord.commands.prefix}") String prefix) {
        return new PrefixTokenizer(prefix);
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
        return new UserValidator() {
            @Override
            public boolean validate(Member user, BotCommand command) { return true; }

            @Override
            public boolean validate(User user, BotCommand command) {
                return true;
            }
        };
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
    public CommandHandler commandHandler(Tokenizer tokenizer, MessageValidator messageValidator, CommandValidator commandValidator,
                                         UserValidator userValidator, CommandList commandList) {
        return new DefaultCommandHandler(tokenizer, messageValidator, commandValidator, userValidator, commandList);
    }
}
