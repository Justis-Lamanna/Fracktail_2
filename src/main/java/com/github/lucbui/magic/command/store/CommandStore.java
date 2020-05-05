package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.context.CommandCreateContext;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

public interface CommandStore {
    Mono<BotCommand> getCommand(Tokens tokens, CommandUseContext ctx);
    void addCommand(BotCommand botCommand, CommandCreateContext ctx);
}
