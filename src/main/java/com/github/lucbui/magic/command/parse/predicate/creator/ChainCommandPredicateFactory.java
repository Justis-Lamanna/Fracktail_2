package com.github.lucbui.magic.command.parse.predicate.creator;

import com.github.lucbui.magic.command.parse.predicate.CommandPredicate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ChainCommandPredicateFactory implements CommandPredicateFactory {
    private final List<CommandPredicateLink> links;

    public ChainCommandPredicateFactory(List<CommandPredicateLink> links) {
        this.links = links;
    }

    public ChainCommandPredicateFactory(CommandPredicateLink... links) {
        this.links = Arrays.asList(links);
    }

    @Override
    public CommandPredicate createCommandPredicate(Method method) {
        return links.stream()
                .reduce(
                        CommandPredicate.trueIdentity(),
                        (seed, link) -> link.addPredicate(seed, method),
                        CommandPredicate::and);
    }
}
