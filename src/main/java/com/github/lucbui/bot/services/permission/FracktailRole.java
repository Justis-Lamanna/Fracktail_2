package com.github.lucbui.bot.services.permission;

import com.github.lucbui.magic.validation.BotRole;

import java.util.Arrays;
import java.util.Optional;

/**
 * Constants representing the various roles in the application.
 */
public enum FracktailRole implements BotRole {
    OWNER("owner"),
    ADMIN("admin"),
    BAN("ban");

    private final String name;

    FracktailRole(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public static Optional<FracktailRole> getRoleByName(String name) {
        return Arrays.stream(FracktailRole.values())
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
