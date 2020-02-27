package com.github.lucbui.calendarfun.command.store;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface CommandHandler {
    Mono<Void> handleMessageCreateEvent(MessageCreateEvent event);
}
