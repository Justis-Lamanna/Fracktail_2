package com.github.lucbui.magic.command.store;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Describes an object which can handle MessageCreateEvents
 */
public interface CommandHandler<IN> {
    /**
     * Handle a MessageCreateEvent
     * @param event The event to handle
     * @return A Mono which completes when handling is completed.
     */
    Mono<Void> handleMessageCreateEvent(IN event);
}
