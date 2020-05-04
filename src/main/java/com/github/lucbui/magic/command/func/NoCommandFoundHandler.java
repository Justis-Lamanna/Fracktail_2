package com.github.lucbui.magic.command.func;

import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public interface NoCommandFoundHandler {
    Mono<BotCommand> getDefaultBotCommand(Tokens tokens);

    static NoCommandFoundHandler doNothing() {
        return tokens -> Mono.empty();
    }
}
