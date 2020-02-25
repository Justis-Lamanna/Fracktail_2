package com.github.lucbui.calendarfun.config;

import com.github.lucbui.calendarfun.command.CommandStoreImpl;
import com.github.lucbui.calendarfun.command.CommandStore;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Presence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Value("${discord.token}")
    private String token;

    @Bean
    public DiscordClient bot(CommandStore commandStore) {
        DiscordClient bot = new DiscordClientBuilder(token)
                .setInitialPresence(Presence.doNotDisturb())
                .build();

        bot.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(commandStore::handleMessageCreateEvent)
                .subscribe();

        return bot;
    }
}
