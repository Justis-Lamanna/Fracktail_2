package com.github.lucbui.bot.config;

import com.github.lucbui.magic.command.store.CommandHandler;
import com.github.lucbui.magic.validation.BasicPermissionsService;
import com.github.lucbui.magic.validation.validators.ChainCreateMessageValidator;
import com.github.lucbui.magic.validation.validators.CooldownCommandValidator;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import com.github.lucbui.magic.validation.validators.NotBotUserMessageValidator;
import com.github.lucbui.magic.validation.validators.UserPermissionValidator;
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
    @Bean
    public CreateMessageValidator createMessageValidator(@Value("${discord.commands.timeout:30s}") Duration timeout, @Value("${discord.permissions.preload:}") String preload) {
        return new ChainCreateMessageValidator(
                new NotBotUserMessageValidator(),
                new CooldownCommandValidator(timeout),
                new UserPermissionValidator(new BasicPermissionsService(preload)));
    }

    @Bean
    public DiscordClient bot(CommandHandler commandHandler, @Value("${discord.token}") String token) {
        DiscordClient bot = new DiscordClientBuilder(token)
                .setInitialPresence(Presence.doNotDisturb())
                .build();

        bot.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(commandHandler::handleMessageCreateEvent)
                .subscribe();

        return bot;
    }
}
