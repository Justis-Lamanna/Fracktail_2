package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.token.Tokens;

import java.util.function.BiPredicate;

public interface BotBehaviorMerger {
    BotMessageBehavior mergeBehavior(BotMessageBehavior oldBehavior, BotMessageBehavior newBehavior, BiPredicate<Tokens, CommandUseContext> newBehaviorPredicate);
}
