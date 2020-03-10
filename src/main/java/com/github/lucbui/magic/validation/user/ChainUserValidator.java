package com.github.lucbui.magic.validation.user;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;

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

    @Override
    public boolean validate(User user, BotCommand command) {
        return userValidators.stream()
                .allMatch(userValidator -> userValidator.validate(user, command));
    }
}
