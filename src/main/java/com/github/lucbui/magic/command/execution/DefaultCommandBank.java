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
    private Map<String, BotCommand> bank;

    public DefaultCommandBank(Supplier<Map<String, BotCommand>> bankSupplier) {
        bank = bankSupplier.get();
    }

    @Override
    public Optional<BotCommand> getCommandById(String name) {
        return Optional.ofNullable(bank.get(name));
    }

    @Override
    public Mono<BotCommand> getCommand(Tokens tokens, CommandUseContext ctx) {
        BotCommand cmd = bank.get(tokens.getCommand());
        if(cmd != null && cmd.testContext(ctx)) {
            return Mono.just(cmd);
        }
        return Mono.empty();
    }

    @Override
    public Flux<BotCommand> getAllCommands(CommandUseContext ctx) {
        return Flux.fromIterable(bank.values())
                .filter(c -> c.testContext(ctx));
    }

    @Override
    public Flux<BotCommand> getAllCommands() {
        return Flux.fromIterable(bank.values());
    }

    @Override
    public void addCommand(BotCommand command) {
        addCommandToMap(command.getName(), command);
        Arrays.stream(command.getAliases())
                .forEach(name -> addCommandToMap(name, command));
    }

    @Override
    public void updateCommand(BotCommand newCommand) {
        BotCommand oldCommand = bank.get(newCommand.getName());
        if(oldCommand == null) {
            addCommand(newCommand);
            return;
        }

        bank.put(newCommand.getName(), newCommand);
        Arrays.stream(newCommand.getAliases())
                .forEach(name -> bank.put(newCommand.getName(), newCommand));
    }

    private void addCommandToMap(String name, BotCommand command) {
        if(bank.containsKey(name)) {
            throw new IllegalArgumentException("Attempted to overwrite command " + name);
        }
        bank.put(name, command);
    }
}
