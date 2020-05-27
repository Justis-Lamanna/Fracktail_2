package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;

import java.lang.reflect.Method;
import java.util.function.BiPredicate;

public interface BotCommandBehaviorPredicateCreator {
    BiPredicate<Tokens, CommandUseContext> createBehaviorPredicate(Method method);
}
