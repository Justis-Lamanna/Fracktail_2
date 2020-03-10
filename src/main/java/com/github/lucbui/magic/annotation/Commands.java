package com.github.lucbui.magic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as containing commands
 * Only beans annotated with @Commands will be scanned for {@link com.github.lucbui.magic.annotation.Command}s
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Commands {
}
