package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.annotation.CommandParams;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokens;

import java.lang.reflect.Method;
import java.util.function.BiPredicate;

public class CommandParamCountBehaviorPredicateCreator implements BotCommandBehaviorPredicateCreator {
    @Override
    public BiPredicate<Tokens, CommandUseContext> createBehaviorPredicate(Method method) {
        BiPredicate<Tokens, CommandUseContext> identity = (t, c) -> true;
        if(method.isAnnotationPresent(CommandParams.class)) {
            CommandParams cp = method.getAnnotation(CommandParams.class);
            identity = identity.and(new CommandParamsPredicate(cp.value(), cp.comparison()));
        }
        return identity;
    }
}
