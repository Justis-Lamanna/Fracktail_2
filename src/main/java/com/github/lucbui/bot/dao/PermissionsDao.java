package com.github.lucbui.bot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionsDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getGlobalPermissions(String userId) {
        return jdbcTemplate.queryForList(
                "SELECT permission " +
                        "FROM permissions " +
                        "WHERE guild_snowflake = 'GLOBAL' " +
                        "AND user_snowflake = ?",
                new Object[]{userId}, String.class);
    }

    public List<String> getLocalPermissions(String guildId, String userId) {
        return jdbcTemplate.queryForList(
                "SELECT permission " +
                        "FROM permissions " +
                        "WHERE guild_snowflake IN (?,'GLOBAL') " +
                        "AND user_snowflake = ?",
                new Object[]{userId, guildId}, String.class);
    }

    public int addGlobalPermission(String userId, String permission) {
        return jdbcTemplate.update("INSERT INTO permissions (user_snowflake, guild_snowflake, permission) VALUES (?, GLOBAL, ?);", userId, permission);
    }

    public int addLocalPermission(String guildId, String userId, String permission) {
        return jdbcTemplate.update("INSERT INTO permissions (user_snowflake, guild_snowflake, permission) VALUES (?, ?, ?);", userId, guildId, permission);
    }

    public int removeGlobalPermission(String userId, String permission) {
        return jdbcTemplate.update("DELETE FROM permissions WHERE user_snowflake = ? AND guild_snowflake = GLOBAL AND permission = permission;", userId, permission);
    }

    public int removeLocalPermission(String guildId, String userId, String permission) {
        return jdbcTemplate.update("DELETE FROM permissions WHERE user_snowflake = ? AND guild_snowflake = ? AND permission = permission;", userId, guildId, permission);
    }

    public int removeAllPermissionsForGuild(String guildId, String userId) {
        return jdbcTemplate.update("DELETE FROM permissions WHERE user_snowflake = ? AND guild_snowflake = ?", userId, guildId);
    }

    public int removeAllPermissions(String userId) {
        return jdbcTemplate.update("DELETE FROM permissions WHERE user_snowflake = ?;", userId);
    }
}
