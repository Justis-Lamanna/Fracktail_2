package com.github.lucbui.magic.command.parse.predicate.creator;

import com.github.lucbui.magic.command.parse.predicate.CommandPredicate;

import java.lang.reflect.Method;

public interface CommandPredicateFactory {
    CommandPredicate createCommandPredicate(Method method);

    static CommandPredicateFactory alwaysTrue() {
        return m -> CommandPredicate.trueIdentity();
    }
}
