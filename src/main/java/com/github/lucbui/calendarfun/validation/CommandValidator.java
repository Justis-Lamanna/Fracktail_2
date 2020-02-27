package com.github.lucbui.calendarfun.validation;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;

public interface CommandValidator {
    default boolean validate(MessageCreateEvent event, BotCommand command) {
        return event.getMember().map(member -> validate(member, command)).orElse(false);
    }

    boolean validate(Member user, BotCommand command);
}
