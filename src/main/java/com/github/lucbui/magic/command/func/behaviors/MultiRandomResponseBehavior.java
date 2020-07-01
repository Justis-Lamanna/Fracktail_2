package com.github.lucbui.magic.command.func.behaviors;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

public class MultiRandomResponseBehavior implements BotMessageBehavior {
    private final List<String> choices;
    private final Random randomness;

    public MultiRandomResponseBehavior(List<String> choices, Random randomness) {
        this.choices = choices;
        this.randomness = randomness;
    }

    public MultiRandomResponseBehavior(List<String> choices) {
        this.choices = choices;
        this.randomness = new Random();
    }

    @Override
    public Mono<Boolean> execute(Tokens tokens, CommandUseContext ctx) {
        int opt = randomness.nextInt(choices.size());
        return ctx.respond(choices.get(opt)).thenReturn(true);
    }
}
