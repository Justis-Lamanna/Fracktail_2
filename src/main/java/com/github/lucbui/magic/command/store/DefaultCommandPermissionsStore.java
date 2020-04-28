package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.PermissionsPredicate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultCommandPermissionsStore implements CommandPermissionsStore {
    private Map<BotCommand, PermissionsPredicate> mapping = Collections.synchronizedMap(new HashMap<>());
    private PermissionsPredicate defaultPermissions;

    public DefaultCommandPermissionsStore(PermissionsPredicate defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }

    @Override
    public boolean hasPermissionsForCommand(BotCommand botCommand, Set<String> userPermissions) {
        return this.mapping.computeIfAbsent(botCommand, key -> defaultPermissions).validatePermissions(userPermissions);
    }

    @Override
    public void setPermissionsForCommand(BotCommand botCommand, PermissionsPredicate permissions) {
        this.mapping.put(botCommand, permissions);
    }
}
