package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;

import java.util.function.Predicate;

public class BotCommand {
    private String name;
    private String[] aliases;
    private BotMessageBehavior behavior;
    private Predicate<CommandUseContext> contextPredicate;

    public BotCommand(String name, String[] aliases, BotMessageBehavior behavior) {
        this.name = name;
        this.aliases = aliases;
        this.behavior = behavior;
        this.contextPredicate = ctx -> true;
    }

    public BotCommand(String name, String[] aliases, BotMessageBehavior behavior, Predicate<CommandUseContext> contextPredicate) {
        this.name = name;
        this.aliases = aliases;
        this.behavior = behavior;
        this.contextPredicate = contextPredicate;
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

    public BotMessageBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(BotMessageBehavior behavior) {
        this.behavior = behavior;
    }

    public Predicate<CommandUseContext> getContextPredicate() {
        return contextPredicate;
    }

    public void setContextPredicate(Predicate<CommandUseContext> contextPredicate) {
        this.contextPredicate = contextPredicate;
    }

    public boolean testContext(CommandUseContext ctx) {
        return contextPredicate == null || contextPredicate.test(ctx);
    }
}
