package com.github.lucbui.calendarfun.validation;

import discord4j.core.object.util.Snowflake;

import java.util.Set;

public interface PermissionsService {
    Set<String> getPermissions(Snowflake userId);

    void addPermission(Snowflake userId, String permission);

    void removePermission(Snowflake userId, String permission);
}