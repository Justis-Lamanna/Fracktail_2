package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class DefaultCommandBank implements CommandBank {
    private Map<String, BCommand> bank;

    public DefaultCommandBank(Supplier<Map<String, BCommand>> bankSupplier) {
        bank = bankSupplier.get();
    }

    @Override
    public Optional<BCommand> getCommandById(String name) {
        return Optional.ofNullable(bank.get(name));
    }

    @Override
    public Mono<BCommand> getCommand(Tokens tokens, CommandUseContext ctx) {
        BCommand cmd = bank.get(tokens.getCommand());
        if(cmd != null && cmd.testContext(ctx)) {
            return Mono.just(cmd);
        }
        return Mono.empty();
    }

    @Override
    public Flux<BCommand> getAllCommands(CommandUseContext ctx) {
        return Flux.fromIterable(bank.values())
                .filter(c -> c.testContext(ctx));
    }

    @Override
    public Flux<BCommand> getAllCommands() {
        return Flux.fromIterable(bank.values());
    }

    @Override
    public void addCommand(BCommand command) {
        addCommandToMap(command.getName(), command);
        Arrays.stream(command.getAliases())
                .forEach(name -> addCommandToMap(name, command));
    }

    private void addCommandToMap(String name, BCommand command) {
        if(bank.containsKey(name)) {
            throw new IllegalArgumentException("Attempted to overwrite command " + name);
        }
        bank.put(name, command);
    }
}
