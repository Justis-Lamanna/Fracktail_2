package com.github.lucbui.magic.command.func;

import com.github.lucbui.magic.command.context.CommandUseContext;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Describes the behavior of a command
 */
public interface BotMessageBehavior {
    /**
     * Execute command behavior
     * @param ctx The context of the message usage
     * @return A Mono which completes when behavior completes
     */
    Mono<Void> execute(CommandUseContext ctx);
}
