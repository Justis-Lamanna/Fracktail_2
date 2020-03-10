package com.github.lucbui.magic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter annotation to inject basic sender information
 * @deprecated {@link com.github.lucbui.magic.annotation.Sender @Sender} provides far more useful information.
 * @see com.github.lucbui.magic.annotation.Sender
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Deprecated
public @interface BasicSender {
}
