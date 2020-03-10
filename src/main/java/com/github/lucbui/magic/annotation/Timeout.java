package com.github.lucbui.magic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * Marks a command as requiring a certain time to elapse between calls
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Timeout {
    /**
     * The number of units to wait between calls
     * @return The number of units to wait between calls
     */
    long value();

    /**
     * The unit to use
     * By default, the unit is seconds
     * @return The unit to use
     */
    ChronoUnit unit() default ChronoUnit.SECONDS;
}
