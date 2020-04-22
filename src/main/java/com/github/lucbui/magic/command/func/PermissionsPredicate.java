package com.github.lucbui.magic.command.func;

import java.util.Set;

public interface PermissionsPredicate {
    boolean validatePermissions(Set<String> permissionsUserHas);

    static PermissionsPredicate allPermitted() {
        return (perms) -> true;
    }
}
