package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.token.Tokenizer;
import reactor.core.publisher.Mono;

public class ParamAnnotationExtractor implements Extractor {
    private final Tokenizer tokenizer;

    public ParamAnnotationExtractor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Mono<Object> extract(CommandUseContext ctx) {
        return null;
    }
}
