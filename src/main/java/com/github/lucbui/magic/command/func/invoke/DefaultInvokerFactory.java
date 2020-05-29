package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.exception.BotException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class DefaultInvokerFactory implements InvokerFactory {
    @Override
    public Invoker getInvokerFor(Object beanToInvoke, Method method) {
        if (method.getReturnType() == null) {
            return new NoReturnInvoker(beanToInvoke, method);
        } else if (method.getReturnType().equals(String.class)) {
            return new StringReturnInvoker(beanToInvoke, method);
        } else if (method.getReturnType().equals(Mono.class)) {
            return new MonoReturnInvoker(beanToInvoke, method);
        } else if (method.getReturnType().equals(Flux.class)) {
            return new FluxReturnInvoker(beanToInvoke, method);
        } else {
            throw new BotException("Return is unexpected type, expected is String, Mono, Flux, or none.");
        }
    }
}
