package com.github.lucbui.magic.validation.user;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;

public interface UserValidator {
    boolean validate(Member user, BotCommand command);
    boolean validate(User user, BotCommand command);
}
