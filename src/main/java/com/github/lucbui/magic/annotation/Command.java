package com.github.lucbui.magic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a bot command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Command {
    /**
     * The name of the command.
     * If none are specified, the method name is used instead.
     * @return The command primary name.
     */
    String value() default "";

    /**
     * One or more command aliases.
     * @return A list of aliases for this command.
     */
    String[] aliases() default {};
}
