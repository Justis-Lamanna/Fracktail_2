package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.PermissionsPredicate;

import java.util.Set;

public interface CommandPermissionsStore {
    boolean hasPermissionsForCommand(BotCommand botCommand, Set<String> userPermissions);
    void setPermissionsForCommand(BotCommand botCommand, PermissionsPredicate permissions);
}
