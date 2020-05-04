package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.token.Tokens;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A list of usable bot commands
 */
public class CommandList {
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
    public void addCommand(BotCommand command) {
        addCommandToMap(command.getName(), command);
        Arrays.stream(command.getAliases())
                .forEach(name -> addCommandToMap(name, command));
    }

    /**
     * Get a command by token
     * @param tokens The tokens for this command
     * @return The corresponding command, or null if none.
     */
    public Optional<BotCommand> getCommand(Tokens tokens) {
        return commandMap.get(tokens.getCommand())
                .stream()
                .filter(bc -> bc.testTokens(tokens))
                .findFirst();
    }

    public boolean isCommand(String commandName) {
        return commandMap.containsKey(commandName);
    }

    public List<BotCommand> getCommandsForName(String commandName) {
        return commandMap.get(commandName);
    }

    private void addCommandToMap(String name, BotCommand command) {
        commandMap.computeIfAbsent(name, n -> new ArrayList<>()).add(command);
    }

    public List<BotCommand> getAllCommands() {
        return commandMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public String getNormalizedName(String nameOrAlias) {
        List<BotCommand> commands = commandMap.get(nameOrAlias);
        if(!commands.isEmpty()) {
            return commands.get(0).getName();
        }
        return null;
    }
}
