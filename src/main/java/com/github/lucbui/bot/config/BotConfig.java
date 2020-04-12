package com.github.lucbui.bot.config;

import com.github.lucbui.magic.command.store.CommandHandler;
import com.github.lucbui.magic.validation.BasicPermissionsService;
import com.github.lucbui.magic.validation.validators.*;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private Environment environment;

    @Bean("userPermissionValidator")
    public UserPermissionValidator userPermissionValidator() {
        Map<Snowflake, Set<String>> preload = new HashMap<>();
        preload.put(Snowflake.of("248612704019808258"), Collections.singleton("admin"));
        return new UserPermissionValidator(new BasicPermissionsService(preload));
    }

    @Bean
    public CreateMessageValidator createMessageValidator(@Value("${discord.commands.timeout:30s}") Duration timeout) {
        return new ChainCreateMessageValidator(
                new NotBotUserMessageValidator(),
                new CooldownCommandValidator(timeout),
                userPermissionValidator());
    }

    @Bean
    public DiscordClient bot(CommandHandler commandHandler, @Value("${discord.token}") String token) {
        Presence presence;
        if(ArrayUtils.contains(environment.getActiveProfiles(), "prod")) {
            presence = Presence.doNotDisturb();
        } else {
            presence = Presence.doNotDisturb(Activity.playing("in Debug Mode!"));
        }

        DiscordClient bot = new DiscordClientBuilder(token)
                .setInitialPresence(presence)
                .build();

        bot.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(commandHandler::handleMessageCreateEvent)
                .subscribe();

        return bot;
    }
}
