package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.validation.PermissionsService;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;

import java.util.Set;

/**
 * Uses a PermissionsService to validate if a command can be used
 */
public class UserPermissionValidator implements CreateMessageValidator {
    private final PermissionsService permissionsService;

    /**
     * Initialize a UserPermissionValidator
     * @param permissionsService The PermissionsService to use when validating.
     */
    public UserPermissionValidator(PermissionsService permissionsService){
        this.permissionsService = permissionsService;
    }

    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        Set<String> permissionsCommandNeeds = command.getPermissions();
        if(permissionsCommandNeeds.isEmpty()){
            return true;
        } else {
            Set<String> permissionsUserHas = permissionsService.getPermissions(
                    event.getGuildId().orElse(null),
                    event.getMessage().getAuthor().map(User::getId).orElse(null));
            return permissionsUserHas.containsAll(permissionsCommandNeeds);
        }
    }
}
