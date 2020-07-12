package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.context.CommandUseContext;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class MonoReturnInvoker implements Invoker {
    private final Object objToInvokeOn;
    private final Method methodToInvoke;

    public MonoReturnInvoker(Object objToInvokeOn, Method methodToInvoke) {
        this.objToInvokeOn = objToInvokeOn;
        this.methodToInvoke = methodToInvoke;
    }

    @Override
    public Mono<Boolean> invoke(CommandUseContext ctx, Object[] params) throws Exception {
        Mono<?> response = (Mono<?>) methodToInvoke.invoke(objToInvokeOn, params);
        if (response == null) {
            return Mono.empty();
        }
        return response.flatMap(res -> {
            if (res == null) {
                return Mono.empty();
            }
            if (res instanceof String) {
                return ctx.respond((String) res).thenReturn(true);
            } else {
                return Mono.just(true);
            }
        });
    }
}
