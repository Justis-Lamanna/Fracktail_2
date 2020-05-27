package com.github.lucbui.magic.command.execution;

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
