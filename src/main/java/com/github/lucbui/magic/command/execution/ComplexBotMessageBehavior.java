package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class ComplexBotMessageBehavior implements BotMessageBehavior {
    private PredicateFunctionPair pair;
    private BotMessageBehavior elze;

    public ComplexBotMessageBehavior(BiPredicate<Tokens, CommandUseContext> predicate, BotMessageBehavior function) {
        pair = new PredicateFunctionPair(predicate, function);
        elze = null;
    }

    private ComplexBotMessageBehavior(PredicateFunctionPair pair) {
        this.pair = pair;
        elze = null;
    }

    public void orElse(BotMessageBehavior function) {
        elze = function;
    }

    public List<PredicateFunctionPair> flatten() {
        List<PredicateFunctionPair> flattened = new ArrayList<>();
        _flatten(flattened);
        return flattened;
    }

    private void _flatten(List<PredicateFunctionPair> list) {
        list.add(pair);
        if(elze != null) {
            if(elze instanceof ComplexBotMessageBehavior) {
                ((ComplexBotMessageBehavior) elze)._flatten(list);
            } else {
                list.add(new PredicateFunctionPair((t,c) -> true, elze));
            }
        } else {
            list.add(null);
        }
    }

    public static ComplexBotMessageBehavior unflatten(List<PredicateFunctionPair> list) {
        ComplexBotMessageBehavior top = null;
        ComplexBotMessageBehavior current = null;
        for(PredicateFunctionPair item : list) {
            if(item != null) {
                ComplexBotMessageBehavior nuu = new ComplexBotMessageBehavior(item);
                if (top == null) {
                    top = current = nuu;
                } else {
                    current.orElse(nuu);
                    current = nuu;
                }
            }
        }
        return top;
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
