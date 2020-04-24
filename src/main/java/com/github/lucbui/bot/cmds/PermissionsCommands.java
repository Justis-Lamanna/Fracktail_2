package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.services.permission.FracktailRole;
import com.github.lucbui.bot.services.translate.TranslateHelper;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.util.DiscordUtils;
import com.github.lucbui.magic.validation.BotRole;
import com.github.lucbui.magic.validation.PermissionsService;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Commands
public class PermissionsCommands {
    @Autowired
    private PermissionsService permissionsService;

    @Autowired
    private TranslateService translateService;

    @Autowired
    private DiscordClient bot;

    @Command
    public Mono<String> permissions(MessageCreateEvent event) {
        Snowflake guildId = event.getGuildId().orElse(null);
        Snowflake userId = event.getMessage().getAuthor().map(User::getId).orElse(null);
        if(userId == null) {
            return Mono.empty();
        }
        return permissionsService.getPermissions(guildId, userId)
                .collectList()
                .map(permissions -> {
                    String permissionsList = permissions.stream().map(BotRole::getName).collect(Collectors.joining(", "));
                    return translateService.getFormattedString("permissions.text", permissions.size(), permissionsList);
                });
    }

    @Command
    @Permissions("admin")
    @Permissions("owner")
    public Mono<String> addpermission(MessageCreateEvent evt, @Param(0) String userId, @Param(1) String guildIdOrPermission, @Param(2) String permissionOrNull) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("addpermission")));
        Snowflake guildSnowflake;
        BotRole permission;
        if(permissionOrNull == null) {
            //!addpermission user permission. Guild is implied to be the current one.
            permission = FracktailRole.getRoleByName(guildIdOrPermission)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("addpermission")));
            guildSnowflake = evt.getGuildId().orElseThrow(() -> translateService.getStringException("addpermission.validation.dm"));
        } else {
            //!addpermission user guild permission.
            permission = FracktailRole.getRoleByName(permissionOrNull)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("addpermission")));
            guildSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(guildIdOrPermission)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("addpermission")));
        }
        return Mono.zip(bot.getUserById(userSnowflake), bot.getGuildById(guildSnowflake))
                .zipWhen(userGuild ->
                        permissionsService.addPermission(userGuild.getT1().getId(), userGuild.getT2().getId(), permission)
                            .thenReturn(permission), (tuple2, elem) -> Tuples.of(tuple2.getT1(), tuple2.getT2(), elem))
                .map(userGuildPermissions -> translateService.getFormattedString("addpermission.text.local",
                        userGuildPermissions.getT1().getUsername(),
                        userGuildPermissions.getT2().getName(),
                        userGuildPermissions.getT3().getName()))
                .onErrorResume(ex -> translateService.getStringMono("validation.unknownUserOrGuild"));
    }

    @Command
    @Permissions("admin")
    @Permissions("owner")
    public Mono<String> removepermission(MessageCreateEvent evt, @Param(0) String userId, @Param(1) String guildIdOrPermission, @Param(2) String permissionOrNull) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("removepermission")));
        Snowflake guildSnowflake;
        BotRole permission;
        if(permissionOrNull == null) {
            //Guild is implied to be the current one. Fail if this is a DM.
            permission = FracktailRole.getRoleByName(guildIdOrPermission)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("removepermission")));
            guildSnowflake = evt.getGuildId().orElseThrow(() -> translateService.getStringException("removepermission.validation.dm"));
        } else {
            permission = FracktailRole.getRoleByName(permissionOrNull)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("removepermission")));
            guildSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("removepermission")));
        }
        return Mono.zip(bot.getUserById(userSnowflake), bot.getGuildById(guildSnowflake))
                .zipWhen(userGuild ->
                        permissionsService.removePermission(userGuild.getT1().getId(), userGuild.getT2().getId(), permission)
                                .thenReturn(permission), (tuple2, elem) -> Tuples.of(tuple2.getT1(), tuple2.getT2(), elem))
                .map(userGuildPermissions -> translateService.getFormattedString("removepermission.text.local",
                        userGuildPermissions.getT1().getUsername(),
                        userGuildPermissions.getT2().getName(),
                        userGuildPermissions.getT3().getName()))
                .onErrorResume(ex -> translateService.getStringMono(TranslateHelper.UNKNOWN_USER_OR_GUILD));
    }

    @Command
    @Permissions("owner")
    public Mono<String> addglobalpermission(@Param(0) String userId, @Param(1) String permission) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("addglobalpermission")));
        BotRole role = FracktailRole.getRoleByName(permission)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("addglobalpermission")));
        return bot.getUserById(userSnowflake)
                .zipWhen(user -> permissionsService.addPermission(null, user.getId(), role).thenReturn(role))
                .map(userPermissions -> translateService.getFormattedString("addglobalpermission.text.global",
                        userPermissions.getT1().getUsername(),
                        userPermissions.getT2().getName()))
                .onErrorResume(ex -> translateService.getStringMono(TranslateHelper.UNKNOWN_USER_OR_GUILD));
    }

    @Command
    @Permissions("owner")
    public Mono<String> removeglobalpermission(@Param(0) String userId, @Param(1) String permission) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("removeglobalpermission")));
        BotRole role = FracktailRole.getRoleByName(permission)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("removeglobalpermission")));
        return bot.getUserById(userSnowflake)
                .zipWhen(user -> permissionsService.removePermission(null, user.getId(), role).thenReturn(role))
                .map(userPermissions -> translateService.getFormattedString("removeglobalpermission.text.global",
                        userPermissions.getT1().getUsername(),
                        userPermissions.getT2().getName()))
                .onErrorResume(ex -> translateService.getStringMono(TranslateHelper.UNKNOWN_USER_OR_GUILD));
    }

    @Command
    @Permissions("admin")
    @Permissions("owner")
    public Mono<String> ban(MessageCreateEvent evt, @Param(0) String userId, @Param(1) String guildIdOrNull) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("ban")));
        Snowflake guildSnowflake;
        if(guildIdOrNull == null) {
            guildSnowflake = evt.getGuildId().orElseThrow(() -> translateService.getStringException("ban.validation.dm"));
        } else {
            guildSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(guildIdOrNull)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("ban")));
        }

        return Mono.zip(bot.getGuildById(guildSnowflake), bot.getUserById(userSnowflake))
                .flatMap(guildUser -> permissionsService.ban(guildUser.getT1().getId(), guildUser.getT2().getId()).thenReturn(guildUser))
                .flatMap(guildUser -> translateService.getFormattedStringMono("ban.text", guildUser.getT1().getName(), guildUser.getT2().getUsername()));
    }

    @Command
    @Permissions("owner")
    public Mono<String> globalban(MessageCreateEvent evt, @Param(0) String userId) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("globalban")));

        return bot.getUserById(userSnowflake)
                .flatMap(user -> permissionsService.ban(null, user.getId()).thenReturn(user))
                .flatMap(user -> translateService.getFormattedStringMono("globalban.text", user.getUsername()));
    }

    @Command
    @Permissions("admin")
    @Permissions("owner")
    public Mono<String> unban(MessageCreateEvent evt, @Param(0) String userId, @Param(1) String guildIdOrNull) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("unban")));
        Snowflake guildSnowflake;
        if(guildIdOrNull == null) {
            guildSnowflake = evt.getGuildId().orElseThrow(() -> translateService.getStringException("unban.validation.dm"));
        } else {
            guildSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(guildIdOrNull)
                    .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("unban")));
        }

        return Mono.zip(bot.getGuildById(guildSnowflake), bot.getUserById(userSnowflake))
                .flatMap(guildUser -> permissionsService.unban(guildUser.getT1().getId(), guildUser.getT2().getId()).thenReturn(guildUser))
                .flatMap(guildUser -> translateService.getFormattedStringMono("unban.text", guildUser.getT1().getName(), guildUser.getT2().getUsername()));
    }

    @Command
    @Permissions("owner")
    public Mono<String> globalunban(MessageCreateEvent evt, @Param(0) String userId) {
        Snowflake userSnowflake = DiscordUtils.toSnowflakeFromMentionOrLiteral(userId)
                .orElseThrow(() -> translateService.getStringException(TranslateHelper.usageKey("globalunban")));

        return bot.getUserById(userSnowflake)
                .flatMap(user -> permissionsService.unban(null, user.getId()).thenReturn(user))
                .flatMap(user -> translateService.getFormattedStringMono("globalunban.text", user.getUsername()));
    }
}