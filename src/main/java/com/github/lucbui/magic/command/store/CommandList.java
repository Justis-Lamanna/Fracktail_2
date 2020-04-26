package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.*;

/**
 * A list of usable bot commands
 */
public class CommandList {
    private final Map<String, BotCommand> commandMap;

    /**
     * Create a CommandList
     * @param commandStoreMapFactory A factory which is used to create the internal map
     */
    public CommandList(CommandStoreMapFactory commandStoreMapFactory) {
        this.commandMap = commandStoreMapFactory.getMap();
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
        Arrays.stream(command.getNames())
                .forEach(name -> commandMap.put(name, command));
    }

    /**
     * Remove commands by name
     * @param names The names of the commands to delete.
     */
    public void removeCommand(String... names) {
        Arrays.stream(names)
                .forEach(commandMap::remove);
    }

    /**
     * Get a command by name
     * @param name The name of the command
     * @return The corresponding command, or null if none.
     */
    public BotCommand getCommand(String name) {
        return commandMap.get(name);
    }

    /**
     * Get all commands on the list
     * @return All commands, in no particular order
     */
    public List<BotCommand> getAllCommands() {
        return new ArrayList<>(commandMap.values());
    }
}
