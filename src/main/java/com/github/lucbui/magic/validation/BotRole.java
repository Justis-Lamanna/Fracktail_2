package com.github.lucbui.magic.validation;

/**
 * A common interface for bot roles
 */
public interface BotRole {
    /**
     * Get the name of this role
     * @return The name of this role
     */
    String getName();

    /**
     * Check if this role is a 'banned' role
     * @return True, if this role marks a user as banned.
     */
    boolean isBannedRole();
}
