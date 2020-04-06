package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

/**
 * A Message Validator that verifies a user is not a bot.
 */
public class NotBotUserMessageValidator extends BasicMessageValidator {
    @Override
    public boolean validateBool(MessageCreateEvent event, BotCommand command) {
        return !event.getMessage().getAuthor().map(User::isBot).orElse(true);
    }
}
