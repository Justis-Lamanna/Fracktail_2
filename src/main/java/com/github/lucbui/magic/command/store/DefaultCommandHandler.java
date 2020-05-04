package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.NoCommandFoundHandler;
import com.github.lucbui.magic.exception.CommandValidationException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * A Default Command Handler, used if no other is specified.
 * This basic handler handles:
 * Tokenizing messages
 * Validating commands
 * Executing commands
 */
public class DefaultCommandHandler implements CommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandHandler.class);

    private final Tokenizer tokenizer;
    private final CreateMessageValidator createMessageValidator;
    private final CommandList commandList;

    private NoCommandFoundHandler noCommandFoundHandler;

    /**
     * Initialize DefaultCommandHandler
     * @param tokenizer The tokenizer to use
     * @param createMessageValidator A validator which validates a command should be handled
     * @param commandList A list of commands
     * @param noCommandFoundHandler A handler to use if a command is attempted to be used incorrectly.
     */
    public DefaultCommandHandler(Tokenizer tokenizer, CreateMessageValidator createMessageValidator, CommandList commandList, NoCommandFoundHandler noCommandFoundHandler) {
        this.tokenizer = tokenizer;
        this.createMessageValidator = createMessageValidator;
        this.commandList = commandList;
        this.noCommandFoundHandler = noCommandFoundHandler;
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(!event.getMember().isPresent() && !event.getMessage().getAuthor().map(User::isBot).orElse(false)){
            LOGGER.info("Received message from {} via DM: {}",
                    event.getMessage().getAuthor().map(User::getUsername).orElse("???"),
                    event.getMessage().getContent().orElse("???"));
        }

        return tokenizer.tokenizeToMono(event)
                .flatMap(tokens -> commandList.getCommand(tokens).map(Mono::just)
                        .orElseGet(() -> noCommandFoundHandler.getDefaultBotCommand(tokens)))
                .filterWhen(cmd -> createMessageValidator.validate(event, cmd))
                .doOnNext(cmd -> LOGGER.info("Executing command {} from {}",
                        cmd.getName(),
                        event.getMessage().getAuthor().map(User::getUsername).orElse("???")))
                .flatMap(cmd -> cmd.getBehavior().execute(event))
                .onErrorResume(CommandValidationException.class, ex -> DiscordUtils.respond(event.getMessage(), ex.getMessage()))
                .onErrorResume(ex -> {
                    LOGGER.error("Error handling message", ex);
                    return DiscordUtils.respond(event.getMessage(), "I'm sorry, I encountered an exception. Please check the logs.");
                });
    }
}
