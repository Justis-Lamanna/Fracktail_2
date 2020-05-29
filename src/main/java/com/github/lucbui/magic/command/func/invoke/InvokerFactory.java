package com.github.lucbui.magic.command.func.invoke;

import java.lang.reflect.Method;

public interface InvokerFactory {
    Invoker getInvokerFor(Object beanToInvoke, Method method);
}
