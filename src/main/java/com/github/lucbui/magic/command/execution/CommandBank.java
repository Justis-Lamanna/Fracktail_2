package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CommandBank {
    Optional<BCommand> getCommandById(String name);
    Mono<BCommand> getCommand(Tokens tokens, CommandUseContext ctx);
    Flux<BCommand> getAllCommands(CommandUseContext ctx);
    Flux<BCommand> getAllCommands();

    void addCommand(BCommand command);
}
