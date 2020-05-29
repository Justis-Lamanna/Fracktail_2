package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.context.CommandUseContext;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class StringReturnInvoker implements Invoker {
    private final Object objToInvokeOn;
    private final Method methodToInvoke;

    public StringReturnInvoker(Object objToInvokeOn, Method methodToInvoke) {
        this.objToInvokeOn = objToInvokeOn;
        this.methodToInvoke = methodToInvoke;
    }

    @Override
    public Mono<Boolean> invoke(CommandUseContext ctx, Object[] params) throws Exception {
        String response = (String) methodToInvoke.invoke(objToInvokeOn, params);
        return response == null ? Mono.empty() : ctx.respond(response).thenReturn(true);
    }
}
