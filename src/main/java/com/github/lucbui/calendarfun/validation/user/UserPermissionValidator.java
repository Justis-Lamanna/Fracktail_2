package com.github.lucbui.calendarfun.validation.user;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.validation.PermissionsService;
import discord4j.core.object.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
