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
    private PermissionsPredicate permissionsPredicate;

    /**
     * Create a Bot command
     * @param names The names of the command
     * @param helpText The command's help text
     * @param behavior The command's behavior
     * @param permissionsPredicate The command's permission tester, if any
     */
    public BotCommand(String[] names, String helpText, BotMessageBehavior behavior, PermissionsPredicate permissionsPredicate) {
        this.names = names;
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissionsPredicate = permissionsPredicate;
    }

    /**
     * Create a Bot command
     * @param name The name of the command
     * @param helpText The command's help text
     * @param behavior The command's behavior
     * @param permissionsPredicate The command's permissions, if any
     */
    public BotCommand(String name, String helpText, BotMessageBehavior behavior, PermissionsPredicate permissionsPredicate) {
        this.names = new String[]{name};
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissionsPredicate = permissionsPredicate;
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
        this.permissionsPredicate = (perms) -> true;
    }

    /**
     * Get the names of the command
     * @return The names of the command
     */
    public String[] getNames() {
        return names;
    }

    /**
     * Get the primary name of the command
     * @return The names of the command
     */
    public String getPrimaryName() {
        return names[0];
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
     * Check if a user with the input permissions can use this command
     * @param userPermissions The permissions the user has
     * @return True if the command can be used
     */
    public boolean hasPermissions(Set<String> userPermissions) {
        return permissionsPredicate.validatePermissions(userPermissions);
    }
}
