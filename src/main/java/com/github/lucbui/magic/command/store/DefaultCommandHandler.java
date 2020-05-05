package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.exception.CommandValidationException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

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
    private final CommandStore commandStore;

    /**
     * Initialize DefaultCommandHandler
     * @param tokenizer The tokenizer to use
     * @param createMessageValidator A validator which validates a command should be handled
     * @param commandStore A list of commands
     */
    public DefaultCommandHandler(Tokenizer tokenizer, CreateMessageValidator createMessageValidator, CommandStore commandStore) {
        this.tokenizer = tokenizer;
        this.createMessageValidator = createMessageValidator;
        this.commandStore = commandStore;
    }

    @Override
    public Mono<Void> handleMessageCreateEvent(MessageCreateEvent event) {
        if(!event.getMember().isPresent() && !event.getMessage().getAuthor().map(User::isBot).orElse(false)){
            LOGGER.info("Received message from {} via DM: {}",
                    event.getMessage().getAuthor().map(User::getUsername).orElse("???"),
                    event.getMessage().getContent().orElse("???"));
        }

        return tokenizer.tokenizeToMono(event)
                .flatMap(tokens -> commandStore.getCommand(tokens, CommandUseContext.from(event)))
                .filterWhen(cmd -> createMessageValidator.validate(event, cmd))
                .doOnNext(cmd -> LOGGER.info("Executing command {} from {}",
                        cmd.getName(),
                        event.getMessage().getAuthor().map(User::getUsername).orElse("???")))
                .flatMap(cmd -> event.getMessage().getChannel().flatMapMany(mc -> mc.typeUntil(cmd.getBehavior().execute(event))).then())
                .onErrorResume(CommandValidationException.class, ex -> DiscordUtils.respond(event.getMessage(), ex.getMessage()))
                .onErrorResume(ex -> {
                    LOGGER.error("Error handling message", ex);
                    return DiscordUtils.respond(event.getMessage(), "I'm sorry, I encountered an exception. Please check the logs.");
                });
    }
}
