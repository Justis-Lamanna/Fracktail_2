package com.github.lucbui.calendarfun.validation;

import com.github.lucbui.calendarfun.annotation.Command;
import com.github.lucbui.calendarfun.annotation.Sender;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HashMapPermissionsService implements PermissionsService {
    private Map<Snowflake, Set<String>> permissions;

    public HashMapPermissionsService(@Value("${discord.permissions.preload:}") String preload) {
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
                addPermission(Snowflake.of(kv[0]), kv[1]);
            } else {
                throw new IllegalArgumentException("Invalid preload");
            }
        }
    }

    @Override
    public Set<String> getPermissions(Snowflake userId) {
        return new HashSet<>(permissions.computeIfAbsent(userId, key -> new HashSet<>()));
    }

    @Override
    public void addPermission(Snowflake userId, String permission) {
        permissions.computeIfAbsent(userId, key -> new HashSet<>()).add(permission);
    }

    @Override
    public void removePermission(Snowflake userId, String permission) {
        permissions.computeIfAbsent(userId, key -> new HashSet<>()).remove(permission);
    }

    @Command(help = "Display all permissions you have.")
    public String permissions(@Sender Member member) {
        Set<String> permissions = getPermissions(member.getId());
        if(permissions.isEmpty()) {
            return "You have no permissions.";
        } else {
            return "Your permissions are: " + permissions.stream().sorted().collect(Collectors.joining(", ")) + ".";
        }
    }
}
