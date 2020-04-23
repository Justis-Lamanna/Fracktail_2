package com.github.lucbui.bot.services.permission;

import com.github.lucbui.bot.dao.PermissionsDao;
import com.github.lucbui.magic.validation.BotRole;
import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.object.util.PermissionSet;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SQLitePermissionsService implements PermissionsService {
    @Autowired
    private PermissionsDao permissionsDao;

    @Override
    public Flux<? extends BotRole> getPermissions(Snowflake guildId, Snowflake userId) {
        if(guildId == null) {
            return Flux.fromIterable(permissionsDao.getGlobalPermissions(userId.asString()))
                    .flatMap(role -> Mono.justOrEmpty(FracktailRole.getRoleByName(role)));
        } else {
            return Flux.fromIterable(permissionsDao.getLocalPermissions(guildId.asString(), userId.asString()))
                    .flatMap(role -> Mono.justOrEmpty(FracktailRole.getRoleByName(role)));
        }
    }

    @Override
    public Mono<Void> addPermission(Snowflake guildId, Snowflake userId, BotRole permission) {
        return Mono.fromRunnable(() -> {
            if (guildId == null) {
                permissionsDao.addGlobalPermission(userId.asString(), permission.getName());
            } else {
                permissionsDao.addLocalPermission(guildId.asString(), userId.asString(), permission.getName());
            }
        });
    }

    @Override
    public Mono<Void> removePermission(Snowflake guildId, Snowflake userId, BotRole permission) {
        return Mono.fromRunnable(() -> {
            if (guildId == null) {
                permissionsDao.removeGlobalPermission(userId.asString(), permission.getName());
            } else {
                permissionsDao.removeLocalPermission(guildId.asString(), userId.asString(), permission.getName());
            }
        });
    }

    @Override
    public Mono<Void> ban(Snowflake guildId, Snowflake userId) {
        return Mono.fromRunnable(() -> {
            if(guildId == null) {
                permissionsDao.removeAllPermissions(userId.asString());
                permissionsDao.addGlobalPermission(userId.asString(), FracktailRole.BAN.getName());
            } else {
                permissionsDao.removeAllPermissionsForGuild(guildId.asString(), userId.asString());
                permissionsDao.addLocalPermission(guildId.asString(), userId.asString(), FracktailRole.BAN.getName());
            }
        });
    }

    @Override
    public Mono<Void> unban(Snowflake guildId, Snowflake userId) {
        return Mono.fromRunnable(() -> {
            if(guildId == null) {
                permissionsDao.removeGlobalPermission(userId.asString(), FracktailRole.BAN.getName());
            } else {
                permissionsDao.removeLocalPermission(guildId.asString(), userId.asString(), FracktailRole.BAN.getName());
            }
        });
    }
}
