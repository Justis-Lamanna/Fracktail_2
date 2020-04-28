package com.github.lucbui.magic.config;

import com.github.lucbui.magic.command.CommandAnnotationProcessor;
import com.github.lucbui.magic.command.CommandProcessorBuilder;
import com.github.lucbui.magic.command.func.postprocessor.TimeoutPostProcessor;
import com.github.lucbui.magic.command.store.*;
import com.github.lucbui.magic.token.PrefixTokenizer;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.PermissionsService;
import com.github.lucbui.magic.validation.validators.LocalCooldownCommandValidator;
import com.github.lucbui.magic.validation.validators.UserPermissionValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public CommandList commandList(@Value("${discord.commands.caseInsensitive:false}") boolean caseInsensitive) {
        return CommandList.withCase(caseInsensitive);
    }

    @Bean
    @ConditionalOnProperty("discord.commands.prefix")
    @ConditionalOnMissingBean
    public Tokenizer tokenizer(@Value("${discord.commands.prefix}") String prefix) {
        return new PrefixTokenizer(prefix);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({Tokenizer.class, CommandList.class})
    public CommandAnnotationProcessor commandAnnotationProcessor(Tokenizer tokenizer, CommandList commandList) {
        return new CommandProcessorBuilder(tokenizer, commandList)
                .withDefaultParameterExtractors()
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({Tokenizer.class, CommandList.class})
    public CommandHandler commandHandler(Tokenizer tokenizer, CommandList commandList) {
        return new CommandHandlerBuilder(tokenizer, commandList).build();
    }

    @Bean
    @ConditionalOnBean(PermissionsService.class)
    public UserPermissionValidator userPermissionValidator(PermissionsService permissionsService) {
        return new UserPermissionValidator(permissionsService);
    }

    /*
    Timeout-related functionality. Can be disabled via discord.timeout.enabled:false
     */

    @Bean
    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
    public CommandTimeoutStore commandTimeoutStore(@Value("${discord.timeout.global:0s}") Duration globalTimeout) {
        return new DefaultCommandTimeoutStore(globalTimeout);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
    public LocalCooldownCommandValidator cooldownCommandValidator(CommandTimeoutStore commandTimeoutStore) {
        return new LocalCooldownCommandValidator(commandTimeoutStore);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
    @ConditionalOnBean(CommandTimeoutStore.class)
    public TimeoutPostProcessor timeoutPostProcessor(CommandTimeoutStore store) {
        return new TimeoutPostProcessor(store);
    }
}
