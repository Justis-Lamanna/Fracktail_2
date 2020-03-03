package com.github.lucbui.calendarfun.config;

import com.github.lucbui.calendarfun.command.store.CommandHandler;
import com.github.lucbui.calendarfun.validation.BasicPermissionsService;
import com.github.lucbui.calendarfun.validation.PermissionsService;
import com.github.lucbui.calendarfun.validation.command.CommandValidator;
import com.github.lucbui.calendarfun.validation.command.CooldownCommandValidator;
import com.github.lucbui.calendarfun.validation.message.MessageValidator;
import com.github.lucbui.calendarfun.validation.message.NotBotUserMessageValidator;
import com.github.lucbui.calendarfun.validation.user.UserPermissionValidator;
import com.github.lucbui.calendarfun.validation.user.UserValidator;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Presence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BotConfig {
    @Value("${discord.token}")
    private String token;

    @Value("${discord.timeout:30s}")
    private Duration timeout;

    @Value("${discord.permissions.preload:}")
    private String preload;

    @Bean
    public MessageValidator messageValidator() {
        return new NotBotUserMessageValidator();
    }

    @Bean
    public CommandValidator commandValidator() {
        return new CooldownCommandValidator(timeout);
    }

    @Bean
    public UserValidator userValidator() {
        return new UserPermissionValidator(new BasicPermissionsService(preload));
    }

    @Bean
    public DiscordClient bot(CommandHandler commandHandler) {
        DiscordClient bot = new DiscordClientBuilder(token)
                .setInitialPresence(Presence.doNotDisturb())
                .build();

        bot.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(commandHandler::handleMessageCreateEvent)
                .subscribe();

        return bot;
    }
}
