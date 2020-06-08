package com.github.lucbui.magic.config;

import com.github.lucbui.magic.command.execution.CommandBank;
import com.github.lucbui.magic.command.execution.CommandHandler;
import com.github.lucbui.magic.command.execution.DefaultCommandBank;
import com.github.lucbui.magic.command.execution.DefaultDiscordCommandHandler;
import com.github.lucbui.magic.command.func.BotCommandProcessor;
import com.github.lucbui.magic.command.func.extract.ExtractorFactory;
import com.github.lucbui.magic.command.func.invoke.CommandFallback;
import com.github.lucbui.magic.command.func.invoke.InvokerFactory;
import com.github.lucbui.magic.command.parse.CommandAnnotationProcessor;
import com.github.lucbui.magic.command.parse.CommandProcessorBuilder;
import com.github.lucbui.magic.command.parse.predicate.creator.CommandPredicateFactory;
import com.github.lucbui.magic.token.PrefixTokenizer;
import com.github.lucbui.magic.token.Tokenizer;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

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
    public CommandBank commandBank(@Value("${discord.commands.caseInsensitive:false}") boolean caseInsensitive) {
        return new DefaultCommandBank(caseInsensitive ? CaseInsensitiveMap::new: HashMap::new);
    }

    @Bean
    @ConditionalOnProperty("discord.commands.prefix")
    @ConditionalOnMissingBean
    public Tokenizer tokenizer(@Value("${discord.commands.prefix}") String prefix) {
        return new PrefixTokenizer(prefix);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandAnnotationProcessor commandAnnotationProcessor(
            Tokenizer tokenizer,
            CommandBank commandBank,
            List<BotCommandProcessor> processors,
            @Autowired(required = false) ExtractorFactory extractorFactory,
            @Autowired(required = false) InvokerFactory invokerFactory,
            @Autowired(required = false) CommandPredicateFactory commandPredicateFactory) {
        return new CommandProcessorBuilder(commandBank, tokenizer)
                .withBotCommandPostProcessors(processors)
                .withParameterExtractor(extractorFactory)
                .withMethodInvoker(invokerFactory)
                .withCommandPredicateFactory(commandPredicateFactory)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandFallback commandFallback() {
        return CommandFallback.doNothing();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandHandler<MessageCreateEvent> commandHandler(Tokenizer tokenizer, CommandBank commandBank, CommandFallback commandFallback) {
        return new DefaultDiscordCommandHandler(tokenizer, commandBank, commandFallback);
    }

    /*
    Timeout-related functionality. Can be disabled via discord.timeout.enabled:false
     */

//    @Bean
//    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
//    @ConditionalOnMissingBean
//    public CommandTimeoutStore commandTimeoutStore(@Value("${discord.timeout.global:0s}") Duration globalTimeout) {
//        return new DefaultCommandTimeoutStore(globalTimeout);
//    }
//
//    @Bean
//    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
//    @ConditionalOnMissingBean
//    @Order(-99)
//    public LocalCooldownCommandValidator cooldownCommandValidator(CommandTimeoutStore commandTimeoutStore) {
//        return new LocalCooldownCommandValidator(commandTimeoutStore);
//    }
//
//    @Bean
//    @ConditionalOnProperty(prefix = "discord.timeout", value = "enabled")
//    @ConditionalOnBean(CommandTimeoutStore.class)
//    @ConditionalOnMissingBean
//    public TimeoutPostProcessor timeoutPostProcessor(CommandTimeoutStore store) {
//        return new TimeoutPostProcessor(store);
//    }
}
