package com.github.lucbui.magic.command.execution;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import reactor.core.publisher.Mono;

public class BotCommand {
    private String name;
    private String[] aliases;
    private BotMessageBehavior behavior;

    public BotCommand(String name, String[] aliases, BotMessageBehavior behavior) {
        this.name = name;
        this.aliases = aliases;
        this.behavior = behavior;
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

    public Mono<Boolean> testContext(CommandUseContext ctx) {
        return behavior.canUseInContext(ctx);
    }
}
