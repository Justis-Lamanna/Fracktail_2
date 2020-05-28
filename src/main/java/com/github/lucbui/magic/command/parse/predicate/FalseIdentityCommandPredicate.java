package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

class FalseIdentityCommandPredicate implements CommandPredicate {
    public static final FalseIdentityCommandPredicate INSTANCE = new FalseIdentityCommandPredicate();

    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return Mono.just(false);
    }

    @Override
    public Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        return Mono.just(false);
    }

    @Override
    public CommandPredicate and(CommandPredicate next) {
        return this; //false && next == false
    }

    @Override
    public CommandPredicate or(CommandPredicate next) {
        return next; //false || next == next
    }

    @Override
    public CommandPredicate not() {
        return TrueIdentityCommandPredicate.INSTANCE;
    }
}
