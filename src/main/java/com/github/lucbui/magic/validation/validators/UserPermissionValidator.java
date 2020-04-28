package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandPermissionsStore;
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
    private final CommandPermissionsStore commandPermissionsStore;

    /**
     * Initialize a UserPermissionValidator
     * @param permissionsService The PermissionsService to use when validating.
     * @param commandPermissionsStore
     */
    public UserPermissionValidator(PermissionsService permissionsService, CommandPermissionsStore commandPermissionsStore){
        this.permissionsService = permissionsService;
        this.commandPermissionsStore = commandPermissionsStore;
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
                return commandPermissionsStore.hasPermissionsForCommand(command,
                        permissionsUserHas.stream().map(BotRole::getName).collect(Collectors.toSet()));
            });
    }
}
