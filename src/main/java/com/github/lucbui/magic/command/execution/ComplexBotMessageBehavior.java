package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

import java.util.function.BiPredicate;

public class ComplexBotMessageBehavior implements BotMessageBehavior {
    private PredicateFunctionPair pair;
    private BotMessageBehavior elze;

    public ComplexBotMessageBehavior(BiPredicate<Tokens, CommandUseContext> predicate, BotMessageBehavior function) {
        pair = new PredicateFunctionPair(predicate, function);
        elze = null;
    }

    public void orElse(BotMessageBehavior function) {
        elze = function;
    }

    @Override
    public Mono<Boolean> execute(Tokens tokens, CommandUseContext ctx) {
        if(pair.predicate.test(tokens, ctx)){
            return pair.function.execute(tokens, ctx);
        }
        return elze == null ? Mono.empty() : elze.execute(tokens, ctx);
    }

    private static class PredicateFunctionPair {
        BiPredicate<Tokens, CommandUseContext> predicate;
        BotMessageBehavior function;

        public PredicateFunctionPair(BiPredicate<Tokens, CommandUseContext> predicate, BotMessageBehavior function) {
            this.predicate = predicate;
            this.function = function;
        }
    }
}
