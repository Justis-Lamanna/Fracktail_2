package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.execution.ComplexBotMessageBehavior;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.token.Tokens;

import java.util.function.BiPredicate;

public class NaiveBotBehaviorMerger implements BotBehaviorMerger {
    @Override
    public BotMessageBehavior mergeBehavior(BotMessageBehavior oldBehavior, BotMessageBehavior newBehavior, BiPredicate<Tokens, CommandUseContext> newBehaviorPredicate) {
        ComplexBotMessageBehavior b = new ComplexBotMessageBehavior(newBehaviorPredicate, newBehavior);
        b.orElse(oldBehavior);
        return b;
    }
}
