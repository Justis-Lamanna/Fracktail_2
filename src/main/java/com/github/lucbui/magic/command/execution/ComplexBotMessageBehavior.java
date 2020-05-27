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
        if(pair.predicate.canUseBehaviorInContext(tokens, ctx)){
            return pair.function.execute(tokens, ctx);
        }
        return elze == null ? Mono.empty() : elze.execute(tokens, ctx);
    }

    @Override
    public boolean canUseInContext(CommandUseContext ctx) {
        return pair.predicate.canUseInContext(ctx) && (elze == null || elze.canUseInContext(ctx));
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
