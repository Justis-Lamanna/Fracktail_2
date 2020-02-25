package com.github.lucbui.calendarfun.command.func;

public class BotCommand {
    private String[] names;
    private String helpText;
    private BotMessageBehavior behavior;

    public BotCommand(String[] names, String helpText, BotMessageBehavior behavior) {
        this.names = names;
        this.helpText = helpText;
        this.behavior = behavior;
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
}
