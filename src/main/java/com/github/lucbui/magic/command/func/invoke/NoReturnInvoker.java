package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.context.CommandUseContext;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class NoReturnInvoker implements Invoker<CommandUseContext, Object[], Mono<Boolean>> {
    private final Object objToInvokeOn;
    private final Method methodToInvoke;

    public NoReturnInvoker(Object objToInvokeOn, Method methodToInvoke) {
        this.objToInvokeOn = objToInvokeOn;
        this.methodToInvoke = methodToInvoke;
    }

    @Override
    public Mono<Boolean> invoke(CommandUseContext ctx, Object[] params) throws Exception {
        methodToInvoke.invoke(objToInvokeOn, params);
        return Mono.just(true);
    }
}
