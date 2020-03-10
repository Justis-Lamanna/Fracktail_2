package com.github.lucbui.magic.validation.user;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;

import java.util.Set;

public class UserPermissionValidator implements UserValidator {
    private final PermissionsService permissionsService;

    public UserPermissionValidator(PermissionsService permissionsService){
        this.permissionsService = permissionsService;
    }

    @Override
    public boolean validate(Member user, BotCommand command) {
        return validate(command, user.getGuildId(), user.getId());
    }

    @Override
    public boolean validate(User user, BotCommand command) {
        return validate(command, null, user.getId());
    }

    private boolean validate(BotCommand command, Snowflake guildId, Snowflake userId) {
        Set<String> permissionsCommandNeeds = command.getPermissions();
        if(permissionsCommandNeeds.isEmpty()){
            return true;
        } else {
            Set<String> permissionsUserHas = permissionsService.getPermissions(guildId, userId);
            return permissionsUserHas.containsAll(permissionsCommandNeeds);
        }
    }
}
