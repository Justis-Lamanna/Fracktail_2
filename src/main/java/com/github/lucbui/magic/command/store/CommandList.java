package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.token.Tokens;
import org.springframework.util.LinkedCaseInsensitiveMap;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A list of usable bot commands
 */
public class CommandList implements CommandStore {
    private final Map<String, List<BotCommand>> commandMap;

    /**
     * Create a CommandList
     * @param commandStoreMapFactory A factory which is used to create the internal map
     */
    public CommandList(Supplier<Map<String, List<BotCommand>>> commandStoreMapFactory) {
        this.commandMap = commandStoreMapFactory.get();
    }

    /**
     * Create a case-insensitive CommandList
     * @return A CommandList with case-insensitive commands
     */
    public static CommandList caseInsensitive() {
        return new CommandList(LinkedCaseInsensitiveMap::new);
    }

    /**
     * Create a case-sensitive CommandList
     * @return A CommandList with case-sensitive commands
     */
    public static CommandList caseSensitive() {
        return new CommandList(HashMap::new);
    }

    /**
     * Create a conditionally case-sensitive or -insensitive CommandList
     * @param caseInsensitive True for case-insensitive list
     * @return A CommandList that's case-insensitive or case-sensitive, depending on input
     */
    public static CommandList withCase(boolean caseInsensitive) {
        return caseInsensitive? caseInsensitive() : caseSensitive();
    }

    /**
     * Add a BotCommand to the list
     * @param command The command to add
     */
    @Override
    public void addCommand(BotCommand command) {
        addCommandToMap(command.getName(), command);
        Arrays.stream(command.getAliases())
                .forEach(name -> addCommandToMap(name, command));
    }

    private void addCommandToMap(String name, BotCommand command) {
        commandMap.computeIfAbsent(name, n -> new ArrayList<>()).add(command);
    }

    @Override
    public Mono<BotCommand> getCommand(Tokens tokens, CommandUseContext ctx) {
        return Mono.justOrEmpty(commandMap.get(tokens.getCommand())
                .stream()
                .filter(bc -> bc.testTokens(tokens))
                .findFirst());
    }
}
