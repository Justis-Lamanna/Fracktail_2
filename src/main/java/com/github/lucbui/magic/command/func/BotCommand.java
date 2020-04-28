package com.github.lucbui.magic.command.func;

/**
 * Encapsulates a bot command
 */
public class BotCommand {
    private String[] names;
    private String helpText;
    private BotMessageBehavior behavior;

    /**
     * Create a Bot command
     * @param names The names of the command
     * @param helpText The command's help text
     * @param behavior The command's behavior
     */
    public BotCommand(String[] names, String helpText, BotMessageBehavior behavior) {
        this.names = names;
        this.helpText = helpText;
        this.behavior = behavior;
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
}
