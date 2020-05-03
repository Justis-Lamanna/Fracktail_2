package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokens;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.util.LinkedCaseInsensitiveMap;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Supplier;

/**
 * A list of usable bot commands
 */
public class CommandList {
    private final Map<String, BotCommand> commandMap;

    /**
     * Create a CommandList
     * @param commandStoreMapFactory A factory which is used to create the internal map
     */
    public CommandList(Supplier<Map<String, BotCommand>> commandStoreMapFactory) {
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
     * Get a command by name
     * @param name The name of the command
     * @return The corresponding command, or null if none.
     * @deprecated This won't work anymore soon.
     */
    @Deprecated
    public BotCommand getCommand(String name) {
        return commandMap.get(name);
    }

    /**
     * Get a command by name
     * @param tokens The tokens for this command
     * @return The corresponding command, or null if none.
     */
    public BotCommand getCommand(Tokens tokens) {
        return commandMap.get(tokens.getCommand());
    }

    /**
     * Get all commands on the list
     * @return All commands, in no particular order
     */
    public List<BotCommand> getAllCommands() {
        return new ArrayList<>(commandMap.values());
    }

    private void addCommandToMap(String name, BotCommand command) {
        if(commandMap.containsKey(name)) {
            throw new BotException("I can't program");
        } else {
            commandMap.put(name, command);
        }
    }
}
