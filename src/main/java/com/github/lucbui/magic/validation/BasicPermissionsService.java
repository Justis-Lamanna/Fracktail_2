package com.github.lucbui.magic.validation;

import discord4j.core.object.util.Snowflake;

import java.util.*;

/**
 * A basic permissions service, backed by a Map.
 */
public class BasicPermissionsService implements PermissionsService {
    private Map<Snowflake, Set<String>> permissions;

    /**
     * Initialize Permissions Service with a preload.
     * Preload is of the format: [User ID]:[permission];[User ID]:[permission];...
     * @param preload The preload to initialize the service with.
     */
    public BasicPermissionsService(String preload) {
        permissions = new HashMap<>();
        if(preload.length() > 0) {
            handlePreload(preload);
        }
    }

    private void handlePreload(String preload) {
        String[] pairs = preload.split(";");
        for(String pair : pairs) {
            String[] kv = pair.split(":");
            if(kv.length == 2) {
                addPermission(null, Snowflake.of(kv[0]), kv[1]);
            } else {
                throw new IllegalArgumentException("Invalid preload");
            }
        }
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
