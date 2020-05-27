package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CommandBank {
    Optional<BotCommand> getCommandById(String name);
    Mono<BotCommand> getCommand(Tokens tokens, CommandUseContext ctx);
    Flux<BotCommand> getAllCommands(CommandUseContext ctx);
    Flux<BotCommand> getAllCommands();

    void addCommand(BotCommand command);
    void updateCommand(BotCommand newCommand);
}
