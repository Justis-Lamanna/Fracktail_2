package com.github.lucbui.calendarfun.validation.message;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.Arrays;

public class ChainMessageValidator implements MessageValidator {

    private MessageValidator[] messageValidators;

    public ChainMessageValidator(MessageValidator... messageValidators) {
        this.messageValidators = messageValidators;
    }

    @Override
    public boolean validate(MessageCreateEvent event) {
        return Arrays.stream(messageValidators).allMatch(v -> v.validate(event));
    }
}
