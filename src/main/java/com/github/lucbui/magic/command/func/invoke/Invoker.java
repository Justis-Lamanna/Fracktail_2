package com.github.lucbui.magic.command.func.invoke;

import com.github.lucbui.magic.command.context.CommandUseContext;
import reactor.core.publisher.Mono;

public interface Invoker {
    Mono<Boolean> invoke(CommandUseContext ctx, Object[] params) throws Exception;
}
