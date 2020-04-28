package com.github.lucbui.magic.command.func;

import java.util.Arrays;
import java.util.Set;

public interface PermissionsPredicate {
    boolean validatePermissions(Set<String> permissionsUserHas);

    static PermissionsPredicate allPermitted() {
        return (perms) -> true;
    }

    static PermissionsPredicate anyOf(String... perms) {
        return (userPermissions) -> Arrays.stream(perms).anyMatch(userPermissions::contains);
    }

    static PermissionsPredicate allOf(String... perms) {
        return (userPermissions) -> Arrays.stream(perms).allMatch(userPermissions::contains);
    }
}
