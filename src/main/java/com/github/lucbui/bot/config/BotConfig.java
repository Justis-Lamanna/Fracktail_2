package com.github.lucbui.bot.config;

import com.github.lucbui.bot.services.voice.RerHandler;
import com.github.lucbui.magic.command.store.CommandHandler;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Presence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Bean
    public DiscordClient bot(CommandHandler commandHandler, RerHandler rerHandler, Presence presence,
                             @Value("${discord.token}") String token) {
        DiscordClient bot = new DiscordClientBuilder(token)
                .setInitialPresence(presence)
                .build();

        bot.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(commandHandler::handleMessageCreateEvent)
                .subscribe();

        bot.getEventDispatcher().on(VoiceStateUpdateEvent.class)
                .flatMap(rerHandler::handleVoiceStateUpdateEvent)
                .subscribe();

        return bot;
    }
}
