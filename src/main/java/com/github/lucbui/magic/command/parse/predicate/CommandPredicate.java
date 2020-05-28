package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public interface CommandPredicate {
    Mono<Boolean> canUseInContext(CommandUseContext ctx);
    Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx);

    default CommandPredicate and(CommandPredicate next) {
        return new AndCommandPredicate(this, next);
    }

    default CommandPredicate or(CommandPredicate next) {
        return new OrCommandPredicate(this, next);
    }

    default CommandPredicate not() {
        return new NotCommandPredicate(this);
    }

    static CommandPredicate trueIdentity() {
        return TrueIdentityCommandPredicate.INSTANCE;
    }

    static CommandPredicate falseIdentity() {
        return FalseIdentityCommandPredicate.INSTANCE;
    }
}
