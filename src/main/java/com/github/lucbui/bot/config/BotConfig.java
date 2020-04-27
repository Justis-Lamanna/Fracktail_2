package com.github.lucbui.bot.config;

import com.github.lucbui.magic.command.store.CommandHandler;
import com.github.lucbui.magic.command.store.CommandHandlerBuilder;
import com.github.lucbui.magic.command.store.CommandList;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.validation.BasicPermissionsService;
import com.github.lucbui.magic.validation.PermissionsService;
import com.github.lucbui.magic.validation.validators.*;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class BotConfig {
    @Bean
    public CommandHandler commandHandler(Tokenizer tokenizer, CommandList commandList,
                                         @Value("${discord.commands.timeout:30s}") Duration timeout,
                                         UserPermissionValidator userPermissionValidator) {
        return new CommandHandlerBuilder(tokenizer, commandList)
                .withValidator(new NotBotUserMessageValidator())
                .withValidator(new CooldownCommandValidator(timeout))
                .withValidator(userPermissionValidator)
                .build();
    }

    @Bean
    public DiscordClient bot(CommandHandler commandHandler,
                             @Value("${discord.token}") String token,
                             @Value("${discord.presence.type:ONLINE}") Status status,
                             @Value("${discord.presence.playing:   }") String playing) {
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
