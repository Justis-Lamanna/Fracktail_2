package com.github.lucbui.calendarfun.validation;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

public class NotBotUserMessageValidator implements MessageValidator {
    @Override
    public boolean validate(MessageCreateEvent event) {
        return !event.getMessage().getAuthor().map(User::isBot).orElse(true);
    }
}
