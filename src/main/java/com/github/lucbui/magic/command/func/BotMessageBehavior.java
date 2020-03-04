package com.github.lucbui.magic.command.func;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface BotMessageBehavior {
    Mono<Void> execute(MessageCreateEvent event);
}