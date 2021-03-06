package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.context.CommandUseContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Collectors;

public class FluxReturnInvoker implements Invoker {
    private final Object objToInvokeOn;
    private final Method methodToInvoke;

    public FluxReturnInvoker(Object objToInvokeOn, Method methodToInvoke) {
        this.objToInvokeOn = objToInvokeOn;
        this.methodToInvoke = methodToInvoke;
    }

    @Override
    public Mono<Boolean> invoke(CommandUseContext ctx, Object[] params) throws Exception {
        Flux<?> response = (Flux<?>) methodToInvoke.invoke(objToInvokeOn, params);
        if (response == null) {
            return Mono.empty();
        }
        return response.filter(Objects::nonNull)
                .map(Objects::toString)
                .collect(Collectors.joining("\n"))
                .flatMap(ctx::respond)
                .thenReturn(true);
    }
}
