package com.github.lucbui.calendarfun.validation.message;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface MessageValidator {
    boolean validate(MessageCreateEvent event);
}
