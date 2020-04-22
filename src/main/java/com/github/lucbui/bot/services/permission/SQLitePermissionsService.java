package com.github.lucbui.bot.services.permission;

import com.github.lucbui.bot.dao.PermissionsDao;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.validation.PermissionsService;
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
    public Flux<String> getPermissions(Snowflake guildId, Snowflake userId) {
        if(guildId == null) {
            return Flux.fromIterable(permissionsDao.getGlobalPermissions(userId.asString()));
        } else {
            return Flux.fromIterable(permissionsDao.getLocalPermissions(guildId.asString(), userId.asString()));
        }
    }

    @Override
    public Mono<Void> addPermission(Snowflake guildId, Snowflake userId, String permission) {
        if(guildId == null) {
            permissionsDao.addGlobalPermission(userId.asString(), permission);
        } else {
            permissionsDao.addLocalPermission(guildId.asString(), userId.asString(), permission);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> removePermission(Snowflake guildId, Snowflake userId, String permission) {
        if(guildId == null) {
            permissionsDao.removeGlobalPermission(userId.asString(), permission);
        } else {
            permissionsDao.removeLocalPermission(guildId.asString(), userId.asString(), permission);
        }
        return Mono.empty();
    }
}
