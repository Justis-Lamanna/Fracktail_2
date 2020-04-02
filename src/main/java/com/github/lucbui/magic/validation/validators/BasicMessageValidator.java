package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public abstract class BasicMessageValidator implements CreateMessageValidator {
    abstract boolean validateBool(MessageCreateEvent event, BotCommand botCommand);

    @Override
    public Mono<Boolean> validate(MessageCreateEvent event, BotCommand command) {
        return Mono.fromSupplier(() -> validateBool(event, command));
    }
}
