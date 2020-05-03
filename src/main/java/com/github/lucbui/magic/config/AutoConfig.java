package com.github.lucbui.magic.config;

import com.github.lucbui.magic.command.CommandAnnotationProcessor;
import com.github.lucbui.magic.command.CommandProcessorBuilder;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import com.github.lucbui.magic.command.func.PermissionsPredicate;
import com.github.lucbui.magic.command.func.postprocessor.*;
import com.github.lucbui.magic.command.store.*;
import com.github.lucbui.magic.token.PrefixTokenizer;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.PermissionsService;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import com.github.lucbui.magic.validation.validators.LocalCooldownCommandValidator;
import com.github.lucbui.magic.validation.validators.NotBotUserMessageValidator;
import com.github.lucbui.magic.validation.validators.UserPermissionValidator;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public Presence presence(@Value("${discord.presence.type:ONLINE}") Status status,
                             @Value("${discord.presence.playing:}") String playing,
                             @Value("${discord.presence.watching:}") String watching,
                             @Value("${discord.presence.listening:}") String listening) {
        Activity activity;
        if(StringUtils.isNotEmpty(playing)) {
            activity = Activity.playing(playing);
        } else if(StringUtils.isNotEmpty(watching)) {
            activity = Activity.watching(watching);
        } else if(StringUtils.isNotBlank(listening)) {
            activity = Activity.listening(listening);
        } else {
            activity = null;
        }

        switch (status) {
            case ONLINE: return activity == null ? Presence.online() : Presence.online(activity);
            case IDLE: return activity == null ? Presence.idle() : Presence.idle(activity);
            case DO_NOT_DISTURB: return activity == null ? Presence.doNotDisturb() : Presence.doNotDisturb(activity);
            default: return Presence.invisible();
        }
    }

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
    public CommandAnnotationProcessor commandAnnotationProcessor(Tokenizer tokenizer, CommandList commandList, List<BotCommandPostProcessor> processors) {
        return new CommandProcessorBuilder(tokenizer, commandList)
                .withDefaultParameterExtractors()
                .withBotCommandPostProcessors(processors)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandHandler commandHandler(Tokenizer tokenizer, CommandList commandList, List<CreateMessageValidator> validators) {
        return new CommandHandlerBuilder(tokenizer, commandList)
                .withValidators(validators)
                .build();
    }

    @Bean
    @Order(-100)
    @ConditionalOnMissingBean
    public NotBotUserMessageValidator notBotUserMessageValidator() {
        return new NotBotUserMessageValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public AliasesPostProcessor aliasesPostProcessor() {
        return new AliasesPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public HelpPostProcessor helpPostProcessor() {
        return new HelpPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ParametersPostProcessor parametersPostProcessor() {
        return new ParametersPostProcessor();
    }

    /*
    Permissions-related functionality. Can be disabled via discord.permissions.enabled:false
     */

    @Bean
    @ConditionalOnProperty(prefix = "discord.permissions", value = "enabled")
    @ConditionalOnMissingBean
    public CommandPermissionsStore commandPermissionsStore(@Value("${discord.permissions.global:}") String[] defaultPermissions) {
        if(defaultPermissions.length == 0) {
            return new DefaultCommandPermissionsStore(PermissionsPredicate.allPermitted());
        } else {
            return new DefaultCommandPermissionsStore(PermissionsPredicate.anyOf(defaultPermissions));
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "discord.permissions", value = "enabled")
    @ConditionalOnBean({PermissionsService.class, CommandPermissionsStore.class})
    @ConditionalOnMissingBean
    @Order(-50)
    public UserPermissionValidator userPermissionValidator(PermissionsService permissionsService, CommandPermissionsStore commandPermissionsStore) {
        return new UserPermissionValidator(permissionsService, commandPermissionsStore);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discord.permissions", value = "enabled")
    @ConditionalOnBean(CommandPermissionsStore.class)
    @ConditionalOnMissingBean
    public PermissionsPostProcessor permissionsPostProcessor(CommandPermissionsStore commandPermissionsStore) {
        return new PermissionsPostProcessor(commandPermissionsStore);
    }

    /*
    Timeout-related functionality. Can be disabled via discord.timeout.enabled:false
     */

    @Bean
    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
    @ConditionalOnMissingBean
    public CommandTimeoutStore commandTimeoutStore(@Value("${discord.timeout.global:0s}") Duration globalTimeout) {
        return new DefaultCommandTimeoutStore(globalTimeout);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
    @ConditionalOnMissingBean
    @Order(-99)
    public LocalCooldownCommandValidator cooldownCommandValidator(CommandTimeoutStore commandTimeoutStore) {
        return new LocalCooldownCommandValidator(commandTimeoutStore);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
    @ConditionalOnBean(CommandTimeoutStore.class)
    @ConditionalOnMissingBean
    public TimeoutPostProcessor timeoutPostProcessor(CommandTimeoutStore store) {
        return new TimeoutPostProcessor(store);
    }
}
