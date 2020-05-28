package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

class TrueIdentityCommandPredicate implements CommandPredicate {
    public static final TrueIdentityCommandPredicate INSTANCE = new TrueIdentityCommandPredicate();

    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        return Mono.just(true);
    }

    @Override
    public CommandPredicate and(CommandPredicate next) {
        return next; //true && next == next
    }

    @Override
    public CommandPredicate or(CommandPredicate next) {
        return this; //true || next == true
    }

    @Override
    public CommandPredicate not() {
        return FalseIdentityCommandPredicate.INSTANCE;
    }
}
