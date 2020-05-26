package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.Params;
import com.github.lucbui.magic.command.context.CommandUseContext;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class ParamsAnnotationParameterExtractor implements ParameterExtractor<CommandUseContext> {
    private Tokenizer tokenizer;

    public ParamsAnnotationParameterExtractor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.isAnnotationPresent(Params.class);
    }

    @Override
    public <OUT> Function<CommandUseContext, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        if(parameter.getType().equals(String[].class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx)
                    .map(Tokens::getParams)
                    .map(arr -> subs(arr, parameter.getAnnotation(Params.class)))
                    .cast(out);
        } else if(parameter.getType().equals(String.class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx)
                    .map(t -> Objects.toString(t.getParamString(), ""))
                    .cast(out);
        }
        throw new IllegalArgumentException("@Params must annotate String[] value");
    }

    private String[] subs(String[] arr, Params annotation) {
        if(annotation.start() == 0 && annotation.end() < 0){
            return arr;
        }
        int start = annotation.start();
        int end = Math.min(annotation.end() < 0 ? arr.length : annotation.end(), arr.length);
        if(start < arr.length) {
            return Arrays.copyOfRange(arr, start, end);
        } else {
            return new String[0];
        }
    }
}
