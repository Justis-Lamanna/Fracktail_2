package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

public class NotBotUserMessageValidator implements CreateMessageValidator {
    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        return !event.getMessage().getAuthor().map(User::isBot).orElse(true);
    }
}
