package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public class NotCommandPredicate implements CommandPredicate {
    private final CommandPredicate current;

    public NotCommandPredicate(CommandPredicate current) {
        this.current = current;
    }

    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return current.canUseInContext(ctx).map(b -> !b);
    }

    @Override
    public Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        return current.canUseBehaviorInContext(tokens, ctx).map(b -> !b);
    }

    @Override
    public CommandPredicate not() {
        return current; //!!this = this
    }
}
