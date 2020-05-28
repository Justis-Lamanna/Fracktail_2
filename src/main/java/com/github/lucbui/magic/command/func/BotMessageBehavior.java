package com.github.lucbui.magic.command.func;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

/**
 * Describes the behavior of a command
 */
public interface BotMessageBehavior {
    /**
     * Execute command behavior
     * @param ctx The context of the message usage
     * @return A Mono which completes when behavior completes
     */
    Mono<Boolean> execute(Tokens tokens, CommandUseContext ctx);

    default Mono<Boolean> canUseInContext(CommandUseContext ctx){
        return Mono.just(true);
    }
}
