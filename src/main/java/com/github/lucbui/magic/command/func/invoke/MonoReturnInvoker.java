package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class MonoReturnInvoker implements Invoker<CommandUseContext, Object[], Mono<Void>> {
    private final Object objToInvokeOn;
    private final Method methodToInvoke;

    public MonoReturnInvoker(Object objToInvokeOn, Method methodToInvoke) {
        this.objToInvokeOn = objToInvokeOn;
        this.methodToInvoke = methodToInvoke;
    }

    @Override
    public Mono<Void> invoke(CommandUseContext ctx, Object[] params) throws Exception {
        Mono<?> response = (Mono<?>) methodToInvoke.invoke(objToInvokeOn, params);
        if (response == null) {
            return Mono.empty();
        }
        return response.flatMap(res -> {
            if (res instanceof String) {
                return ctx.respond((String) res);
            } else {
                return Mono.empty();
            }
        });
    }
}
