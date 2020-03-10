package com.github.lucbui.magic.command.func;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

/**
 * Encapsulates a bot command
 */
public class BotCommand {
    private String[] names;
    private String helpText;
    private BotMessageBehavior behavior;
    private Set<String> permissions;
    private Duration timeout;

    /**
     * Create a Bot command
     * @param names The names of the command
     * @param helpText The command's help text
     * @param behavior The command's behavior
     * @param permissions The command's permissions, if any
     * @param timeout The timeout of the command, if any
     */
    public BotCommand(String[] names, String helpText, BotMessageBehavior behavior, Set<String> permissions, Duration timeout) {
        this.names = names;
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissions = permissions;
        this.timeout = timeout;
    }

    /**
     * Create a Bot command
     * @param name The name of the command
     * @param helpText The command's help text
     * @param behavior The command's behavior
     * @param permissions The command's permissions, if any
     * @param timeout The timeout of the command, if any
     */
    public BotCommand(String name, String helpText, BotMessageBehavior behavior, Set<String> permissions, Duration timeout) {
        this.names = new String[]{name};
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissions = permissions;
        this.timeout = timeout;
    }

    /**
     * Create a Bot command
     * @param name The name of the command
     * @param helpText The command's help text
     * @param behavior The command's behavior
     */
    public BotCommand(String name, String helpText, BotMessageBehavior behavior) {
        this.names = new String[]{name};
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissions = Collections.emptySet();
        this.timeout = null;
    }

    /**
     * Get the names of the command
     * @return The names of the command
     */
    public String[] getNames() {
        return names;
    }

    /**
     * Get the help text of the command
     * @return The help text of the command
     */
    public String getHelpText() {
        return helpText;
    }

    /**
     * Get the behavior of the command
     * @return The behavior of the command
     */
    public BotMessageBehavior getBehavior() {
        return behavior;
    }

    /**
     * Get the permissions of the command
     * @return The permissions of the command
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Get the timeout of the command
     * @return The timeout of the command
     */
    public Duration getTimeout() {
        return timeout;
    }
}
