package com.github.lucbui.magic.validation;

import discord4j.core.object.util.Snowflake;

import java.util.*;

/**
 * A basic permissions service, backed by a Map.
 * Users are identified solely by their ID, and no distinguishing is made between
 * different servers.
 */
public class BasicPermissionsService implements PermissionsService {
    private Map<Snowflake, Set<String>> permissions;

    /**
     * Initialize Permissions Service with a preload.
     * @param preload The preload to initialize the service with.
     */
    public BasicPermissionsService(Map<Snowflake, Set<String>> preload) {
        permissions = new HashMap<>(preload);
    }

    @Override
    public Set<String> getPermissions(Snowflake guildId, Snowflake userId) {
        if(userId == null){
            return Collections.emptySet();
        }
        return new HashSet<>(permissions.computeIfAbsent(userId, key -> new HashSet<>()));
    }

    @Override
    public void addPermission(Snowflake guildId, Snowflake userId, String permission) {
        permissions.computeIfAbsent(userId, key -> new HashSet<>()).add(permission);
    }

    @Override
    public void removePermission(Snowflake guildId, Snowflake userId, String permission) {
        permissions.computeIfAbsent(userId, key -> new HashSet<>()).remove(permission);
    }
}
