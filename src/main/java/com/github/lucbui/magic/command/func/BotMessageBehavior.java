package com.github.lucbui.magic.command.func;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Describes the behavior of a command
 */
public interface BotMessageBehavior {
    /**
     * Execute command behavior
     * @param event The Message event
     * @return A Mono which completes when behavior completes
     */
    Mono<Void> execute(MessageCreateEvent event);
}
