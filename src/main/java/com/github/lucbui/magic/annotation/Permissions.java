package com.github.lucbui.magic.annotation;

import java.lang.annotation.*;

/**
 * Marks a command as requiring certain permissions to run.
 * This interface is repeatable. Each instance of the @Permissions annotation will be "or'ed" together, while each
 * permission in value will be "and'ed" together. So, for instance, if you want users of a command to be `Admin OR Owner`,
 * use:
 * @Permissions("Admin")
 * @Permissions("Owner")
 * And if you want users of a command to be `Admin AND Owner`, use:
 * @Permissions({"Admin", "Owner"})
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(PermissionsGroup.class)
public @interface Permissions {
    /**
     * The permissions a user must have to execute this command
     * @return The permissions a user must have to execute this command
     */
    String[] value();
}
