package com.github.lucbui.magic.command.func.behaviors;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public class SingleResponseBehavior implements BotMessageBehavior {
    private final String response;

    public SingleResponseBehavior(String response) {
        this.response = response;
    }

    @Override
    public Mono<Boolean> execute(Tokens tokens, CommandUseContext ctx) {
        return ctx.respond(response).thenReturn(true);
    }
}
