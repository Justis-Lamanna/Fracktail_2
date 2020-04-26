package com.github.lucbui.magic.config;

import com.github.lucbui.magic.command.CommandAnnotationProcessor;
import com.github.lucbui.magic.command.CommandFieldCallbackFactory;
import com.github.lucbui.magic.command.CommandProcessorBuilder;
import com.github.lucbui.magic.command.store.*;
import com.github.lucbui.magic.token.PrefixTokenizer;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Configuration
public class AutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public CommandList commandList(@Value("${discord.commands.caseInsensitive:false}") boolean caseInsensitive) {
        return CommandList.withCase(caseInsensitive);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandAnnotationProcessor commandAnnotationProcessor(CommandList commandList, Tokenizer tokenizer) {
        return new CommandProcessorBuilder(tokenizer).withCommandList(commandList).build();
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
        return (event, command) -> Mono.just(true);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandHandler commandHandler(Tokenizer tokenizer, CreateMessageValidator createMessageValidator, CommandList commandList) {
        return new DefaultCommandHandler(tokenizer, createMessageValidator, commandList);
    }
}
