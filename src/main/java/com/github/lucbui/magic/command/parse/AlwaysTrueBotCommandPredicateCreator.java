package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.command.context.CommandUseContext;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class AlwaysTrueBotCommandPredicateCreator implements BotCommandPredicateCreator {
    @Override
    public Predicate<CommandUseContext> createCommandPredicate(Method method) {
        return c -> true;
    }
}
