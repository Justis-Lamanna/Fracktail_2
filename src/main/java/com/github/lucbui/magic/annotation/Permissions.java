package com.github.lucbui.magic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command as requiring certain permissions to run
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Permissions {
    /**
     * The permissions a user must have to execute this command
     * @return The permissions a user must have to execute this command
     */
    String[] value();
}
