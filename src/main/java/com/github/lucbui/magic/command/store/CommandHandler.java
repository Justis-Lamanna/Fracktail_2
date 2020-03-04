package com.github.lucbui.magic.command.store;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface CommandHandler {
    Mono<Void> handleMessageCreateEvent(MessageCreateEvent event);
}
