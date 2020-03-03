package com.github.lucbui.calendarfun.validation.user;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Member;

public interface UserValidator {
    boolean validate(Member user, BotCommand command);
}
