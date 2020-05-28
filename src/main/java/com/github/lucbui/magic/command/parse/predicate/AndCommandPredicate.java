package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

class AndCommandPredicate implements CommandPredicate {
    private final CommandPredicate first;
    private final CommandPredicate next;

    public AndCommandPredicate(CommandPredicate first, CommandPredicate next) {
        this.first = first;
        this.next = next;
    }

    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return first.canUseInContext(ctx)
                .flatMap(t -> t ? next.canUseInContext(ctx) : Mono.just(false));
    }

    @Override
    public Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        return first.canUseBehaviorInContext(tokens, ctx)
                .flatMap(t -> t ? next.canUseBehaviorInContext(tokens, ctx) : Mono.just(false));
    }
}
