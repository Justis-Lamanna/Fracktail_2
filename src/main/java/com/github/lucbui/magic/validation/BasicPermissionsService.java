package com.github.lucbui.magic.validation;

import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * A basic permissions service, backed by a Map.
 * Users are identified solely by their ID, and no distinguishing is made between
 * different servers.
 */
public class BasicPermissionsService implements PermissionsService {
    public static final String BANNED = "banned";
    public static final BotRole BANNED_ROLE = () -> BANNED;
    private Map<Snowflake, Set<BotRole>> permissions;

    /**
     * Initialize Permissions Service with a preload.
     * @param preload The preload to initialize the service with.
     */
    public BasicPermissionsService(Map<Snowflake, Set<BotRole>> preload) {
        permissions = new HashMap<>(preload);
    }

    /**
     * Initialize Permissions Service as empty.
     */
    public BasicPermissionsService() {
        permissions = new HashMap<>();
    }

    @Override
    public Flux<? extends BotRole> getPermissions(Snowflake guildId, Snowflake userId) {
        if(userId == null || !permissions.containsKey(userId)){
            return Flux.empty();
        }
        return Flux.fromIterable(permissions.get(userId));
    }

    @Override
    public Mono<Void> addPermission(Snowflake guildId, Snowflake userId, BotRole permission) {
        permissions.computeIfAbsent(userId, key -> new HashSet<>()).add(permission);
        return Mono.empty();
    }

    @Override
    public Mono<Void> removePermission(Snowflake guildId, Snowflake userId, BotRole permission) {
        permissions.computeIfAbsent(userId, key -> new HashSet<>()).remove(permission);
        return Mono.empty();
    }

    @Override
    public Mono<Void> ban(Snowflake guildId, Snowflake userId) {
        permissions.compute(userId, (snowflake, strings) -> Collections.singleton(BANNED_ROLE));
        return Mono.empty();
    }
}
