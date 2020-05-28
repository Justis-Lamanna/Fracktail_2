package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public interface CommandPredicate {
    Mono<Boolean> canUseInContext(CommandUseContext ctx);
    Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx);

    default CommandPredicate and(CommandPredicate next) {
        CommandPredicate first = this;
        return new CommandPredicate() {
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
        };
    }

    static CommandPredicate identity() {
        return new CommandPredicate() {
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
                return next; //Identity and next is equivalent to just next.
            }
        };
    }
}
