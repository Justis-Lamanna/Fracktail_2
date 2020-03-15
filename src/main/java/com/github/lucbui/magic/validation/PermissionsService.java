package com.github.lucbui.magic.validation;

import discord4j.core.object.util.Snowflake;

import java.util.Set;

/**
 * A service which determines a user's permissions.
 * String permissions are arbitrary, depending on your permission needs. For instance, it can return the roles a user
 * has in the server, or arbitrary strings defined by the programmer. The only requirement is that they must be
 * consistent with the {@link com.github.lucbui.magic.annotation.Permissions} annotation.
 */
public interface PermissionsService {
    /**
     * Get a user's permissions by Guild ID and User ID.
     * @param guildId The ID of the guild (server)
     * @param userId The ID of the user
     * @return The set of permissions the user has.
     */
    Set<String> getPermissions(Snowflake guildId, Snowflake userId);

    /**
     * Add a permission for a user.
     * @param guildId The ID of the guild (server)
     * @param userId The ID of the user
     * @param permission The permission to add.
     */
    void addPermission(Snowflake guildId, Snowflake userId, String permission);

    /**
     * Remove a permission for a user.
     * @param guildId The ID of the guild (server)
     * @param userId The ID of the user
     * @param permission The permission to remove.
     */
    void removePermission(Snowflake guildId, Snowflake userId, String permission);
}
