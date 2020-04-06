package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

import java.util.Set;

/**
 * Uses a PermissionsService to validate if a command can be used
 */
public class UserPermissionValidator extends BasicMessageValidator {
    private final PermissionsService permissionsService;

    /**
     * Initialize a UserPermissionValidator
     * @param permissionsService The PermissionsService to use when validating.
     */
    public UserPermissionValidator(PermissionsService permissionsService){
        this.permissionsService = permissionsService;
    }

    @Override
    public boolean validateBool(MessageCreateEvent event, BotCommand command) {
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
