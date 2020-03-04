package com.github.lucbui.magic.validation;

import discord4j.core.object.util.Snowflake;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BasicPermissionsService implements PermissionsService {
    private Map<Snowflake, Set<String>> permissions;

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

//    @Command(help = "Display all permissions you have.")
//    public String permissions(@Sender Member member) {
//        Set<String> permissions = getPermissions(member.getGuildId(), member.getId());
//        if(permissions.isEmpty()) {
//            return "You have no permissions.";
//        } else {
//            return "Your permissions are: " + permissions.stream().sorted().collect(Collectors.joining(", ")) + ".";
//        }
//    }

    @Override
    public Set<String> getPermissions(Snowflake guildId, Snowflake userId) {
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
