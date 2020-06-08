package com.github.lucbui.magic.command.parse.predicate.creator;

import com.github.lucbui.magic.annotation.CommandParams;
import com.github.lucbui.magic.command.parse.predicate.CommandPredicate;
import com.github.lucbui.magic.command.parse.predicate.ParameterCountCommandPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class CommandParamsPredicateLink implements CommandPredicateLink {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandParamsPredicateLink.class);

    @Override
    public CommandPredicate addPredicate(CommandPredicate seed, Method method) {
        if(method.isAnnotationPresent(CommandParams.class)) {
            CommandParams a = method.getAnnotation(CommandParams.class);
            if(LOGGER.isDebugEnabled()) {
                switch (a.comparison()) {
                    case OR_MORE: LOGGER.debug("+- Marking command as accepting {} or more parameters", a.value()); break;
                    case OR_LESS: LOGGER.debug("+- Marking command as accepting {} or fewer parameters", a.value()); break;
                    case EXACTLY: LOGGER.debug("+- Marking command as accepting {} parameters", a.value()); break;
                    default: LOGGER.debug("+- Marking command as accepting {} {} parameters", a.value(), a.comparison()); break;
                }
            }
            return seed.and(new ParameterCountCommandPredicate(a.value(), a.comparison()));
        }
        return seed;
    }
}
