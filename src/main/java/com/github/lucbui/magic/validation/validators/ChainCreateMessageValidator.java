package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.Arrays;

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

    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        return Arrays.stream(validators)
                    .allMatch(v -> v.validate(event, command));
    }
}
