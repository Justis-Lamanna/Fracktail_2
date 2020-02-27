package com.github.lucbui.calendarfun.validation.command;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

public interface CommandValidator {
    boolean validate(MessageCreateEvent event, BotCommand command);
}
