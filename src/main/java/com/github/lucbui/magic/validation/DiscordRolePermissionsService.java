package com.github.lucbui.magic.validation;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A PermissionsService which is coupled to Discord roles.
 * BotRoles have the same name as their corresponding Discord roles. This allows for easier manipulation using
 * Discord's pre-made system.
 *
 * addPermission and removePermission will add and remove the provided role to the user globally, and will fail if
 * the role doesn't already exist. ban and unban will ban and unban the user from the guild entirely, so be careful.
 *
 * All methods will return empty when called with a null Guild ID (for example, in DM usage).
 */
public class DiscordRolePermissionsService implements PermissionsService {
    private DiscordClient client;

    /**
     * Set the client to be used with this permissions service
     * @param client The client to use.
     */
    public void setClient(DiscordClient client) {
        this.client = client;
    }

    @Override
    public Flux<? extends BotRole> getPermissions(Snowflake guildId, Snowflake userId) {
        if(guildId == null) return Flux.empty();
        return client.getMemberById(guildId, userId)
                .flatMap(Member::getBasePermissions)
                .flatMapIterable(PermissionSet::asEnumSet)
                .map(DiscordBotRole::new);
    }

    @Override
    public Mono<Void> addPermission(Snowflake guildId, Snowflake userId, BotRole permission) {
        if(guildId == null) return Mono.empty();
        return client.getGuildById(guildId)
                .flatMapMany(Guild::getRoles)
                .filter(role -> role.getName().equalsIgnoreCase(permission.getName()))
                .singleOrEmpty()
                .map(Role::getId)
                .zipWhen(role -> client.getMemberById(guildId, userId))
                .flatMap(roleMember -> roleMember.getT2().addRole(roleMember.getT1(), "Role added by bot"));
    }

    @Override
    public Mono<Void> removePermission(Snowflake guildId, Snowflake userId, BotRole permission) {
        if(guildId == null) return Mono.empty();
        return client.getGuildById(guildId)
                .flatMapMany(Guild::getRoles)
                .filter(role -> role.getName().equalsIgnoreCase(permission.getName()))
                .singleOrEmpty()
                .map(Role::getId)
                .zipWhen(role -> client.getMemberById(guildId, userId))
                .flatMap(roleMember -> roleMember.getT2().removeRole(roleMember.getT1(), "Role removed by bot"));
    }

    @Override
    public Mono<Void> ban(Snowflake guildId, Snowflake userId) {
        if(guildId == null) return Mono.empty();
        return client.getMemberById(guildId, userId)
                .flatMap(member -> member.ban(spec -> spec.setReason("Banned by bot").setDeleteMessageDays(1)));
    }

    @Override
    public Mono<Void> unban(Snowflake guildId, Snowflake userId) {
        if(guildId == null) return Mono.empty();
        return client.getMemberById(guildId, userId)
                .flatMap(member -> member.unban("Unbanned by bot"));
    }

    public static class DiscordBotRole implements BotRole {
        private Permission permission;

        public DiscordBotRole(Permission permission) {
            this.permission = permission;
        }

        @Override
        public String getName() {
            return permission.name();
        }

        @Override
        public boolean isBannedRole() {
            return false;
        }
    }
}
