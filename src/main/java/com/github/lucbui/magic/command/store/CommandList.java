package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
