package com.github.lucbui.bot.services.permission;

import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//@Service
public class SQLitePermissionsService implements PermissionsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Flux<String> getPermissions(Snowflake guildId, Snowflake userId) {
        List<String> permissions;
        if(guildId == null) {
            permissions = jdbcTemplate.queryForList(
                        "SELECT permission " +
                            "FROM permissions " +
                            "WHERE guild_snowflake = GLOBAL" +
                            "AND user_snowflake = ?",
                    new Object[]{userId.asString()}, String.class);
        } else {
            permissions = jdbcTemplate.queryForList(
                        "SELECT permission " +
                            "FROM permissions " +
                            "WHERE guild_snowflake IN (?,GLOBAL) " +
                            "AND user_snowflake = ?",
                    new Object[]{userId.asString(), guildId.asString()}, String.class);
        }
        return Flux.fromIterable(permissions);
    }

    @Override
    public Mono<Void> addPermission(Snowflake guildId, Snowflake userId, String permission) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> removePermission(Snowflake guildId, Snowflake userId, String permission) {
        return Mono.empty();
    }
}
