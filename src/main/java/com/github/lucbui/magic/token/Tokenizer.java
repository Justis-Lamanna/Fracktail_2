package com.github.lucbui.magic.token;

import com.github.lucbui.magic.command.context.CommandUseContext;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * An algorithm which tokenizes a message
 */
public interface Tokenizer {
    /**
     * Break a message into tokens
     * @param message The message to tokenize
     * @return The tokens
     */
    Tokens tokenize(String message);

    /**
     * Test if a message can be tokenized
     * @param message The message to check
     * @return True, if the message can be tokenized
     */
    boolean isValid(String message);

    /**
     * Tokenize the MessageCreateEvent to a Mono of Tokens.
     * If the Mono is empty, then this tokenizer does not apply.
     * @param event The event to tokenize
     * @return A mono containing the tokens
     */
    @Deprecated
    default Mono<Tokens> tokenizeToMono(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .filter(this::isValid)
                .map(this::tokenize);
    }

    default Mono<Tokens> tokenizeToMono(CommandUseContext ctx) {
        return Mono.justOrEmpty(ctx.getMessage())
                .filter(this::isValid)
                .map(this::tokenize);
    }
}
