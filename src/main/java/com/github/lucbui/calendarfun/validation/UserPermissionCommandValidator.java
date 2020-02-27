package com.github.lucbui.calendarfun.validation;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserPermissionCommandValidator implements CommandValidator, UserCommandValidator {
    private final PermissionsService permissionsService;

    @Autowired
    public UserPermissionCommandValidator(PermissionsService permissionsService){
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

    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        return event.getMember().map(member -> validate(member, command)).orElse(false);
    }
}
