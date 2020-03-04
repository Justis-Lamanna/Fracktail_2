package com.github.lucbui.magic.validation.message;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface MessageValidator {
    boolean validate(MessageCreateEvent event);
}
