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
     * One or more command names.
     * If no names are specified, the name of the method is used instead.
     * @return A list of names for this command.
     */
    String[] value() default {};

    /**
     * Help text
     * This text should explain how the command should be used.
     * @return Help text which explains usage of this command
     */
    String help() default "";
}
