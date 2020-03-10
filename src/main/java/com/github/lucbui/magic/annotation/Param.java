package com.github.lucbui.magic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter annotation to inject a command parameter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    /**
     * The index of the parameter to retrieve.
     * @return The index of the parameter to retrieve.
     */
    int value();
}
