package com.github.lucbui.magic.command.parse.predicate.creator;

import com.github.lucbui.magic.command.parse.predicate.CommandPredicate;

import java.lang.reflect.Method;

public interface CommandPredicateLink {
    CommandPredicate addPredicate(CommandPredicate seed, Method method);
}
