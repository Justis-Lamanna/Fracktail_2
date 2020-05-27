package com.github.lucbui.bot.config;

import com.github.lucbui.bot.services.voice.RerHandler;
import com.github.lucbui.magic.command.execution.CommandHandler;
import com.github.lucbui.magic.command.func.invoke.CommandFallback;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Presence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    private Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);

    @Bean
    public CommandFallback testFallback() {
        return new CommandFallback(
                (t, c) -> c.respond("No Command").thenReturn(true),
                (t, c) -> c.respond("Bad Command").thenReturn(true)
        );
    }

    @Bean
    public DiscordClient bot(CommandHandler<MessageCreateEvent> commandHandler, RerHandler rerHandler, Presence presence,
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

        bot.getEventDispatcher().on(MemberJoinEvent.class)
                .filter(evt -> evt.getMember().getId().equals(evt.getClient().getSelfId().orElse(null)))
                .flatMap(MemberJoinEvent::getGuild)
                .doOnNext(guild -> LOGGER.info("Joined Guild: " + guild.getName()))
                .subscribe();

        bot.getEventDispatcher().on(MemberLeaveEvent.class)
                .filter(evt -> evt.getUser().getId().equals(evt.getClient().getSelfId().orElse(null)))
                .flatMap(MemberLeaveEvent::getGuild)
                .doOnNext(guild -> LOGGER.info("Left Guild: " + guild.getName()))
                .subscribe();

        return bot;
    }
}
