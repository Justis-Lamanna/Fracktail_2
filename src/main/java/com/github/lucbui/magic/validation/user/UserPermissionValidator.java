package com.github.lucbui.magic.validation.user;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.object.entity.Member;

import java.util.Set;

public class UserPermissionValidator implements UserValidator {
    private final PermissionsService permissionsService;

    public UserPermissionValidator(PermissionsService permissionsService){
        this.permissionsService = permissionsService;
    }

    @Override
    public boolean validate(Member user, BotCommand command) {
        Set<String> permissionsCommandNeeds = command.getPermissions();
        if(permissionsCommandNeeds.isEmpty()){
            return true;
        } else {
            Set<String> permissionsUserHas = permissionsService.getPermissions(user.getGuildId(), user.getId());
            return permissionsUserHas.containsAll(permissionsCommandNeeds);
        }
    }
}
