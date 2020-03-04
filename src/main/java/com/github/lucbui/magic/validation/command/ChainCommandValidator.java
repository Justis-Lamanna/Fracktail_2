package com.github.lucbui.magic.validation.command;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.Arrays;

public class ChainCommandValidator implements CommandValidator {

    private CommandValidator[] validators;

    public ChainCommandValidator(CommandValidator... validators) {
        this.validators = validators;
    }

    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        return Arrays.stream(validators).allMatch(v -> v.validate(event, command));
    }
}
