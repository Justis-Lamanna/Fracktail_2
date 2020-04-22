package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.validation.BotRole;
import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

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
    public Mono<Boolean> validate(MessageCreateEvent event, BotCommand command) {
        return permissionsService.getPermissions(
                event.getGuildId().orElse(null),
                event.getMessage().getAuthor().map(User::getId).orElse(null))
            .collect(Collectors.toSet())
            .map(permissionsUserHas -> {
                if(permissionsUserHas.stream().anyMatch(BotRole::isBannedRole)){
                    return false;
                }
                return command.hasPermissions(permissionsUserHas.stream().map(BotRole::getName).collect(Collectors.toSet()));
            });
    }
}
