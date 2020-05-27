package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.func.BotMessageBehavior;
import reactor.core.publisher.Mono;

public class CommandFallback {
    private BotMessageBehavior noCommandFound;
    private BotMessageBehavior commandUsedIncorrectly;

    public CommandFallback(BotMessageBehavior noCommandFound, BotMessageBehavior commandUsedIncorrectly) {
        this.noCommandFound = noCommandFound;
        this.commandUsedIncorrectly = commandUsedIncorrectly;
    }

    public static CommandFallback doNothing() {
        return new CommandFallback((t, c) -> Mono.just(true), (t, c) -> Mono.just(true));
    }

    public BotMessageBehavior getNoCommandFound() {
        return noCommandFound;
    }

    public BotMessageBehavior getCommandUsedIncorrectly() {
        return commandUsedIncorrectly;
    }
}
