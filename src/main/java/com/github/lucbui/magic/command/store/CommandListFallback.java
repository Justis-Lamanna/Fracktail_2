package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommandListFallback {
    Mono<BotCommand> noCommandFound(Tokens tokens, CommandUseContext ctx);
    Mono<BotCommand> commandUsedWrong(Tokens tokens, CommandUseContext ctx,  List<BotCommand> otherCandidates);

    static CommandListFallback doNothing() {
        return new CommandListFallback() {
            @Override
            public Mono<BotCommand> noCommandFound(Tokens tokens, CommandUseContext ctx) {
                return Mono.empty();
            }

            @Override
            public Mono<BotCommand> commandUsedWrong(Tokens tokens, CommandUseContext ctx, List<BotCommand> otherCandidates) {
                return Mono.empty();
            }
        };
    }
}
