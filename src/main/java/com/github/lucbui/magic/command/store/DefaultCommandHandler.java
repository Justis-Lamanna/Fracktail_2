package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.command.CommandValidator;
import com.github.lucbui.magic.validation.message.MessageValidator;
import com.github.lucbui.magic.validation.user.UserValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * A Default Command Handler, used if no other is specified.
 * This basic handler handles:
 * Tokenizing messages
 * Validating commands
 * Executing commands
 */
public class DefaultCommandHandler implements CommandHandler {

    private final Tokenizer tokenizer;
    private final MessageValidator messageValidator;
    private final CommandValidator commandValidator;
    private final UserValidator userValidator;
    private final CommandList commandList;

    /**
     * Initialize DefaultCommandHandler
     * @param tokenizer The tokenizer to use
     * @param messageValidator A validator which validates a message should be handled
     * @param commandValidator A command validator, which validates if a command should be handled
     * @param userValidator A user validator, which validates if a user can use a command
     * @param commandList A list of commands
     */
    public DefaultCommandHandler(Tokenizer tokenizer, MessageValidator messageValidator, CommandValidator commandValidator,
                                 UserValidator userValidator, CommandList commandList) {
        this.tokenizer = tokenizer;
        this.messageValidator = messageValidator;
        this.commandValidator = commandValidator;
        this.userValidator = userValidator;
        this.commandList = commandList;
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(messageValidator.validate(event)){
            return getTokens(event)
                    .map(tokens -> commandList.getCommand(tokens.getCommand()))
                    .map(cmd -> {
                        try {
                            if(validateCommand(event, cmd)) {
                                return cmd.getBehavior().execute(event);
                            } else {
                                return Mono.<Void>empty();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return DiscordUtils.respond(event.getMessage(), "I'm sorry, I encountered an exception. Please check the logs.");
                        }
                    }).orElse(Mono.empty());
        }
        return Mono.empty();
    }

    private Optional<Tokens> getTokens(MessageCreateEvent event) {
        return event.getMessage()
                .getContent()
                .filter(tokenizer::isValid)
                .map(tokenizer::tokenize);
    }

    private boolean validateCommand(MessageCreateEvent event, BotCommand cmd) {
        return commandValidator.validate(event, cmd) && event.getMember().map(usr -> userValidator.validate(usr, cmd)).orElse(false);
    }
}
