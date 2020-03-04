package com.github.lucbui.magic.command.func;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

public class BotCommand {
    private String[] names;
    private String helpText;
    private BotMessageBehavior behavior;
    private Set<String> permissions;
    private Duration timeout;

    public BotCommand(String[] names, String helpText, BotMessageBehavior behavior, Set<String> permissions, Duration timeout) {
        this.names = names;
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissions = permissions;
        this.timeout = timeout;
    }

    public BotCommand(String name, String helpText, BotMessageBehavior behavior, Set<String> permissions, Duration timeout) {
        this.names = new String[]{name};
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissions = permissions;
        this.timeout = timeout;
    }

    public BotCommand(String name, String helpText, BotMessageBehavior behavior) {
        this.names = new String[]{name};
        this.helpText = helpText;
        this.behavior = behavior;
        this.permissions = Collections.emptySet();
        this.timeout = null;
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

    public Duration getTimeout() {
        return timeout;
    }
}
