package com.github.lucbui.calendarfun.validation;

import discord4j.core.object.util.Snowflake;

import java.util.Set;

public interface PermissionsService {
    Set<String> getPermissions(Snowflake guildId, Snowflake userId);

    void addPermission(Snowflake guildId, Snowflake userId, String permission);

    void removePermission(Snowflake guildId, Snowflake userId, String permission);
}
