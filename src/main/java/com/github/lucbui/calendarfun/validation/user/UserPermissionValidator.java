package com.github.lucbui.calendarfun.validation.user;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.validation.PermissionsService;
import com.github.lucbui.calendarfun.validation.command.CommandValidator;
import com.github.lucbui.calendarfun.validation.user.UserValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserPermissionValidator implements UserValidator {
    private final PermissionsService permissionsService;

    @Autowired
    public UserPermissionValidator(PermissionsService permissionsService){
        this.permissionsService = permissionsService;
    }

    @Override
    public boolean validate(Member user, BotCommand command) {
        Set<String> permissionsCommandNeeds = command.getPermissions();
        if(permissionsCommandNeeds.isEmpty()){
            return true;
        } else {
            Set<String> permissionsUserHas = permissionsService.getPermissions(user.getId());
            return permissionsUserHas.containsAll(permissionsCommandNeeds);
        }
    }
}
