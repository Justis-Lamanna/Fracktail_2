package com.github.lucbui.bot.config;

import com.github.lucbui.magic.command.CommandAnnotationProcessor;
import com.github.lucbui.magic.command.CommandProcessorBuilder;
import com.github.lucbui.magic.command.func.postprocessor.TimeoutPostProcessor;
import com.github.lucbui.magic.command.store.CommandHandler;
import com.github.lucbui.magic.command.store.CommandHandlerBuilder;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.validators.*;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Bean
    public CommandHandler commandHandler(Tokenizer tokenizer, CommandList commandList,
                                         LocalCooldownCommandValidator localCooldownCommandValidator,
                                         UserPermissionValidator userPermissionValidator) {
        return new CommandHandlerBuilder(tokenizer, commandList)
                .withValidator(new NotBotUserMessageValidator())
                .withValidator(localCooldownCommandValidator)
                .withValidator(userPermissionValidator)
                .build();
    }

    @Bean
    public CommandAnnotationProcessor commandAnnotationProcessor(Tokenizer tokenizer, CommandList commandList, TimeoutPostProcessor timeoutPostProcessor) {
        return new CommandProcessorBuilder(tokenizer, commandList)
                .withDefaultParameterExtractors()
                .withBotCommandPostProcessor(timeoutPostProcessor)
                .build();
    }

    @Bean
    public DiscordClient bot(CommandHandler commandHandler,
                             @Value("${discord.token}") String token,
                             @Value("${discord.presence.type:ONLINE}") Status status,
                             @Value("${discord.presence.playing:}") String playing) {
        DiscordClient bot = new DiscordClientBuilder(token)
                .setInitialPresence(getPresence(status, playing))
                .build();

        bot.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(commandHandler::handleMessageCreateEvent)
                .subscribe();

        return bot;
    }

    private Presence getPresence(Status status, String playing) {
        if(status == null) {
            return StringUtils.isEmpty(playing) ? Presence.online() : Presence.online(Activity.playing(playing));
        }
        switch(status) {
            case DO_NOT_DISTURB: return StringUtils.isEmpty(playing) ? Presence.doNotDisturb() : Presence.doNotDisturb(Activity.playing(playing));
            case IDLE: return StringUtils.isEmpty(playing) ? Presence.idle() : Presence.idle(Activity.playing(playing));
            case INVISIBLE: return Presence.invisible();
            case ONLINE:
            default: return StringUtils.isEmpty(playing) ? Presence.online() : Presence.online(Activity.playing(playing));
        }
    }
}
