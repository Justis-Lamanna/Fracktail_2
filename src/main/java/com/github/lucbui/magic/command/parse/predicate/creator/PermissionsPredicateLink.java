package com.github.lucbui.magic.command.parse.predicate.creator;

import com.github.lucbui.magic.annotation.Permissions;
import com.github.lucbui.magic.annotation.PermissionsGroup;
import com.github.lucbui.magic.command.parse.predicate.CommandPredicate;
import com.github.lucbui.magic.command.parse.predicate.OwnerCommandPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class PermissionsPredicateLink implements CommandPredicateLink {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionsPredicateLink.class);

    @Override
    public CommandPredicate addPredicate(CommandPredicate seed, Method method) {
        if(method.isAnnotationPresent(PermissionsGroup.class) || method.isAnnotationPresent(Permissions.class)) {
            LOGGER.debug("+- Marking command as owner-only");
            return seed.and(new OwnerCommandPredicate());
        }
        return seed;
    }
}
