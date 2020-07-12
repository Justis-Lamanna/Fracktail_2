package com.github.lucbui.bot.games;

import java.util.Objects;

public class Id {
    private final String identifier;

    public Id(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id id = (Id) o;
        return Objects.equals(identifier, id.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
