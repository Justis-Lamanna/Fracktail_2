package com.github.lucbui.magic.validation.user;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.object.entity.Member;

public interface UserValidator {
    boolean validate(Member user, BotCommand command);
}
