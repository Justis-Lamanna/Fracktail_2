package com.github.lucbui.calendarfun.validation;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.object.entity.Member;

public interface UserCommandValidator {
    boolean validate(Member user, BotCommand command);
}
