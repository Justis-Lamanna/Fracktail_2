package com.github.lucbui.calendarfun.validation.user;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.object.entity.Member;

import java.util.List;

public class ChainUserValidator implements UserValidator {
    private List<UserValidator> userValidators;

    public ChainUserValidator(List<UserValidator> userValidators) {
        this.userValidators = userValidators;
    }

    @Override
    public boolean validate(Member user, BotCommand command) {
        return userValidators.stream()
                .allMatch(userValidator -> userValidator.validate(user, command));
    }
}
