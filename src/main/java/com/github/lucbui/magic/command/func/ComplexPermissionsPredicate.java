package com.github.lucbui.magic.command.func;

import java.util.List;
import java.util.Set;

public class ComplexPermissionsPredicate implements PermissionsPredicate{
    private List<Set<String>> andOrs;

    public ComplexPermissionsPredicate(List<Set<String>> andOrs) {
        this.andOrs = andOrs;
    }

    @Override
    public boolean validatePermissions(Set<String> permissionsUserHas) {
        return andOrs.stream()
                .anyMatch(permissionsUserHas::containsAll);
    }
}
