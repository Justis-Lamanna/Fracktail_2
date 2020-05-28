package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.command.parse.predicate.CommandPredicate;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public class ComplexBotMessageBehavior implements BotMessageBehavior {
    private PredicateFunctionPair pair;
    private BotMessageBehavior elze;

    public ComplexBotMessageBehavior(CommandPredicate predicate, BotMessageBehavior function) {
        pair = new PredicateFunctionPair(predicate, function);
        elze = null;
    }

    public void orElse(BotMessageBehavior function) {
        elze = function;
    }

    @Override
    public Mono<Boolean> execute(Tokens tokens, CommandUseContext ctx) {
        return pair.predicate.canUseBehaviorInContext(tokens, ctx)
                .flatMap(t -> t ? pair.function.execute(tokens, ctx) : (elze == null ? Mono.empty() : elze.execute(tokens, ctx)));
    }

    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return pair.predicate.canUseInContext(ctx)
                .flatMap(t -> t ? (elze == null ? Mono.just(true) : elze.canUseInContext(ctx)) : Mono.just(false));
    }

    private static class PredicateFunctionPair {
        CommandPredicate predicate;
        BotMessageBehavior function;

        public PredicateFunctionPair(CommandPredicate predicate, BotMessageBehavior function) {
            this.predicate = predicate;
            this.function = function;
        }
    }
}
