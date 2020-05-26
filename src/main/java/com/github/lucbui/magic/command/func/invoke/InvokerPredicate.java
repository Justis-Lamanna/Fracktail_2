package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.CommandFieldCallbackFactory;

import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class InvokerPredicate<I1, I2, O> {
    private final Predicate<Method> predicate;
    private final BiFunction<CommandFieldCallbackFactory.CommandFieldCallback, Method, Invoker<I1, I2, O>> invokerCreator;

    public InvokerPredicate(Predicate<Method> predicate, BiFunction<CommandFieldCallbackFactory.CommandFieldCallback, Method, Invoker<I1, I2, O>> invokerCreator) {
        this.predicate = predicate;
        this.invokerCreator = invokerCreator;
    }

    public boolean test(Method method) {
        return predicate.test(method);
    }

    public Invoker<I1, I2, O> getInvoker(CommandFieldCallbackFactory.CommandFieldCallback cfc, Method method) {
        return invokerCreator.apply(cfc, method);
    }
}
