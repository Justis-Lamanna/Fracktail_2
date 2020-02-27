package com.github.lucbui.calendarfun.command.func;

import java.util.Set;

public class BotCommand {
    private String[] names;
    private String helpText;
    private BotMessageBehavior behavior;
    private Set<String> permissions;

    public BotCommand(String[] names, String helpText, BotMessageBehavior behavior, Set<String> permissions) {
        this.names = names;
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissions = permissions;
    }

    public String[] getNames() {
        return names;
    }

    public String getHelpText() {
        return helpText;
    }

    public BotMessageBehavior getBehavior() {
        return behavior;
    }

    public Set<String> getPermissions() {
        return permissions;
    }
}
