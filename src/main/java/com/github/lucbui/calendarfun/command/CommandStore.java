package com.github.lucbui.calendarfun.command;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommandStore {
    Mono<Void> handleMessageCreateEvent(MessageCreateEvent event);

    void addCommand(BotCommand command);

    void removeCommand(String... names);

    BotCommand getCommand(String name);

    List<BotCommand> getAllCommands();
}
