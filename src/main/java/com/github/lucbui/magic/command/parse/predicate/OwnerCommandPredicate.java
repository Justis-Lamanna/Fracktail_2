package com.github.lucbui.magic.command.parse.predicate;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

//TEMPORARY!!!
public class OwnerCommandPredicate implements CommandPredicate {
    @Override
    public Mono<Boolean> canUseInContext(CommandUseContext ctx) {
        return Mono.just(ctx.getUserId().equals("248612704019808258"));
    }

    @Override
    public Mono<Boolean> canUseBehaviorInContext(Tokens tokens, CommandUseContext ctx) {
        return Mono.just(ctx.getUserId().equals("248612704019808258"));
    }
}
