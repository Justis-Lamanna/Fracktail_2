package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.Arrays;

public class ChainCreateMessageValidator implements CreateMessageValidator {
    private CreateMessageValidator[] validators;

    public ChainCreateMessageValidator(CreateMessageValidator... validators) {
        this.validators = validators;
    }

    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        return Arrays.stream(validators)
                    .allMatch(v -> v.validate(event, command));
    }
}
