package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

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

    /**
     * Initialize DefaultCommandHandler
     * @param tokenizer The tokenizer to use
     * @param createMessageValidator A validator which validates a command should be handled
     * @param commandList A list of commands
     */
    public DefaultCommandHandler(Tokenizer tokenizer, CreateMessageValidator createMessageValidator, CommandList commandList) {
        this.tokenizer = tokenizer;
        this.createMessageValidator = createMessageValidator;
        this.commandList = commandList;
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(!event.getMember().isPresent()){
            LOGGER.debug("Received message from {} via DM: {}",
                    event.getMessage().getAuthor().map(User::getUsername).orElse("???"),
                    event.getMessage().getContent().orElse("???"));
        }

        return Mono.justOrEmpty(getTokens(event))
                .flatMap(tokens -> Mono.justOrEmpty(commandList.getCommand(tokens.getCommand())))
                .filterWhen(cmd -> createMessageValidator.validate(event, cmd))
                .doOnNext(cmd -> LOGGER.debug("Executing command {} from {}",
                        cmd.getNames()[0],
                        event.getMessage().getAuthor().map(User::getUsername).orElse("???")))
                .flatMap(cmd -> cmd.getBehavior().execute(event))
                .onErrorResume(ex -> {
                    LOGGER.error("Error handling message", ex);
                    return DiscordUtils.respond(event.getMessage(), "I'm sorry, I encountered an exception. Please check the logs.");
                });
    }

    protected Optional<Tokens> getTokens(MessageCreateEvent event) {
        return event.getMessage()
                .getContent()
                .filter(tokenizer::isValid)
                .map(tokenizer::tokenize);
    }
}
