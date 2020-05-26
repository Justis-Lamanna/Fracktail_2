package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.context.CommandCreateContext;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.token.Tokens;
import org.springframework.util.LinkedCaseInsensitiveMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Supplier;

/**
 * A list of usable bot commands
 */
public class CommandList implements CommandStore {
    private final Map<String, List<BotCommand>> commandMap;
    private final CommandListFallback fallback;

    /**
     * Create a CommandList
     * @param commandStoreMapFactory A factory which is used to create the internal map
     * @param fallback A fallback, to use when no command is found
     */
    public CommandList(Supplier<Map<String, List<BotCommand>>> commandStoreMapFactory, CommandListFallback fallback) {
        this.commandMap = commandStoreMapFactory.get();
        this.fallback = fallback;
    }

    /**
     * Create a CommandList
     * @param commandStoreMapFactory A factory which is used to create the internal map
     */
    public CommandList(Supplier<Map<String, List<BotCommand>>> commandStoreMapFactory) {
        this.commandMap = commandStoreMapFactory.get();
        this.fallback = CommandListFallback.doNothing();
    }

    /**
     * Create a case-insensitive CommandList
     * @return A CommandList with case-insensitive commands
     */
    public static CommandList caseInsensitive() {
        return new CommandList(LinkedCaseInsensitiveMap::new);
    }

    /**
     * Create a case-insensitive CommandList
     * @return A CommandList with case-insensitive commands
     */
    public static CommandList caseInsensitive(CommandListFallback fallback) {
        return new CommandList(LinkedCaseInsensitiveMap::new, fallback);
    }

    /**
     * Create a case-sensitive CommandList
     * @return A CommandList with case-sensitive commands
     */
    public static CommandList caseSensitive() {
        return new CommandList(HashMap::new);
    }

    /**
     * Create a case-sensitive CommandList
     * @return A CommandList with case-sensitive commands
     */
    public static CommandList caseSensitive(CommandListFallback fallback) {
        return new CommandList(HashMap::new, fallback);
    }

    /**
     * Add a BotCommand to the list
     * @param command The command to add
     */
    @Override
    public void addCommand(BotCommand command, CommandCreateContext ctx) {
        addCommandToMap(command.getName(), command);
        Arrays.stream(command.getAliases())
                .forEach(name -> addCommandToMap(name, command));
    }

    private void addCommandToMap(String name, BotCommand command) {
        commandMap.computeIfAbsent(name, n -> new ArrayList<>()).add(command);
    }

    @Override
    public Mono<BotCommand> getCommand(Tokens tokens, CommandUseContext ctx) {
        List<BotCommand> commands = commandMap.get(tokens.getCommand());
        if(commands == null){
            return this.fallback.noCommandFound(tokens, ctx);
        }
        return Mono.justOrEmpty(commands.stream()
                .filter(bc -> bc.testTokens(tokens))
                .findFirst())
                .switchIfEmpty(this.fallback.commandUsedWrong(tokens, ctx, commands));
    }

    @Override
    public Flux<BotCommand> getAllCommands(CommandUseContext ctx) {
        return Flux.fromIterable(commandMap.values())
                .flatMap(Flux::fromIterable);
    }
}
