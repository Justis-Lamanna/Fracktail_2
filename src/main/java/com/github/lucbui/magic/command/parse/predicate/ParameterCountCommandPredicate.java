package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.annotation.ParamsComparison;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public class ParameterCountCommandPredicate implements CommandPredicate {
    private final int value;
    private final ParamsComparison comparison;

    public ParameterCountCommandPredicate(int value, ParamsComparison comparison) {
        this.value = value;
        this.comparison = comparison;
    }

    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        int l = tokens.getParams() == null ? 0 : tokens.getParams().length;
        switch (comparison) {
            case OR_MORE: return Mono.just(l >= value);
            case OR_LESS: return Mono.just(l <= value);
            default: return Mono.just(l == value);
        }
    }
}
