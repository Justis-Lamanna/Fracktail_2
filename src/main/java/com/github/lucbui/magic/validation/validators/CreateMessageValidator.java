package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

public interface CreateMessageValidator {
    boolean validate(MessageCreateEvent event, BotCommand command);
}
