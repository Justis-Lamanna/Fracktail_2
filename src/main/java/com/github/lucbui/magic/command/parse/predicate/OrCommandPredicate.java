package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

class OrCommandPredicate implements CommandPredicate {
    private final CommandPredicate first;
    private final CommandPredicate next;

    public OrCommandPredicate(CommandPredicate first, CommandPredicate next) {
        this.first = first;
        this.next = next;
    }

    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return first.canUseInContext(ctx)
                .flatMap(t -> t ? Mono.just(true) : next.canUseInContext(ctx));
    }

    @Override
    public Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        return first.canUseBehaviorInContext(tokens, ctx)
                .flatMap(t -> t ? Mono.just(true) : next.canUseBehaviorInContext(tokens, ctx));
    }
}
