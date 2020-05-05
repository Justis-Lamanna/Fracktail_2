package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.context.CommandCreateContext;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotMessageBehavior;
import com.github.lucbui.magic.command.func.PermissionsPredicate;
import com.github.lucbui.magic.token.Tokens;
import com.github.lucbui.magic.validation.BotRole;
import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionsBackedCommandStore implements CommandStore {
    private final CommandStore commandStore;
    private final PermissionsService permissionsService;

    private Map<BotCommand, PermissionsPredicate> permissionsStore;

    public PermissionsBackedCommandStore(CommandStore commandStore, PermissionsService permissionsService) {
        this.commandStore = commandStore;
        this.permissionsService = permissionsService;
        this.permissionsStore = new HashMap<>();
    }

    @Override
    public Mono<BotCommand> getCommand(Tokens tokens, CommandUseContext ctx) {
        Mono<Set<String>> userPermissionsMono = permissionsService.getPermissions(Snowflake.of(ctx.getChannelId()), Snowflake.of(ctx.getUserId()))
                .map(BotRole::getName)
                .collect(Collectors.toSet());

        return commandStore.getCommand(tokens, ctx)
                .zipWith(userPermissionsMono)
                .filter(tuple -> {
                    BotCommand cmd = tuple.getT1();
                    Set<String> userPermissions = tuple.getT2();
                    if(permissionsStore.containsKey(cmd)){
                        return permissionsStore.get(cmd).validatePermissions(userPermissions);
                    } else {
                        return true;
                    }
                })
                .map(Tuple2::getT1);
    }

    @Override
    public void addCommand(BotCommand botCommand, CommandCreateContext commandCreateContext) {
        permissionsStore.put(botCommand, commandCreateContext.get("permissions", PermissionsPredicate.class));
        commandStore.addCommand(botCommand, commandCreateContext);
    }
}
