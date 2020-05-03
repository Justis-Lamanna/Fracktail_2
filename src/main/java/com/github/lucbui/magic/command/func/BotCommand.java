package com.github.lucbui.magic.command.func;

import com.github.lucbui.magic.token.Tokens;

import java.util.function.Predicate;

/**
 * Encapsulates a bot command
 */
public class BotCommand {
    private String name;
    private String[] aliases;
    private String helpText;
    private BotMessageBehavior behavior;

    /**
     * Create a Bot command
     * @param name The primary of the command
     * @param behavior The command's behavior
     */
    public BotCommand(String name, BotMessageBehavior behavior) {
        this.name = name;
        this.behavior = behavior;
        this.aliases = new String[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public BotMessageBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(BotMessageBehavior behavior) {
        this.behavior = behavior;
    }
}
