package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.command.context.CommandUseContext;
import reactor.core.publisher.Mono;

public interface Extractor {
    Mono<Object> extract(CommandUseContext ctx);
}
