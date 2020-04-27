package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * A validator which encapsulates multiple validators
 */
public class ChainCreateMessageValidator implements CreateMessageValidator {
    private CreateMessageValidator[] validators;

    /**
     * Create a Chain from other validators.
     * @param validators The chain of validators to use
     */
    public ChainCreateMessageValidator(CreateMessageValidator... validators) {
        this.validators = validators;
    }

    /**
     * Create a Chain from other validators.
     * @param validators The chain of validators to use
     */
    public ChainCreateMessageValidator(List<CreateMessageValidator> validators) {
        this.validators = validators.toArray(new CreateMessageValidator[0]);
    }

    @Override
    public Mono<Boolean> validate(MessageCreateEvent event, BotCommand command) {
        return Flux.just(validators)
            .flatMap(v -> v.validate(event, command))
            .all(b -> b);
    }
}
