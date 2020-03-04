package com.github.lucbui.magic.validation.command;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

public interface CommandValidator {
    boolean validate(MessageCreateEvent event, BotCommand command);
}
