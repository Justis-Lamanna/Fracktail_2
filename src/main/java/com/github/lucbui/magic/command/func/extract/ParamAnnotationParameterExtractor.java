package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Predicate;

public class ParamAnnotationParameterExtractor implements ParameterExtractor<MessageCreateEvent> {
    private Tokenizer tokenizer;

    public ParamAnnotationParameterExtractor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.isAnnotationPresent(Param.class);
    }

    @Override
    public <OUT> Function<MessageCreateEvent, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        int idx = parameter.getAnnotation(Param.class).value();
        if (idx < 0) {
            throw new IllegalArgumentException("@Param value must be non-negative");
        }

        if(parameter.getType().equals(String.class)) {
            return evt -> tokenizer.tokenizeToMono(evt).filter(tokenTester(idx))
                    .map(tokens -> tokens.getParam(idx))
                    .cast(out);
        } else if(parameter.getType().equals(OptionalInt.class)) {
            return evt -> tokenizer.tokenizeToMono(evt).filter(tokenTester(idx))
                    .map(tokens -> tokens.getParam(idx))
                    .map(Integer::parseInt)
                    .map(OptionalInt::of)
                    .onErrorResume(NumberFormatException.class, ex -> Mono.just(OptionalInt.empty()))
                    .defaultIfEmpty(OptionalInt.empty())
                    .cast(out);
        } else if(parameter.getType().equals(Integer.TYPE)) {
            return evt -> tokenizer.tokenizeToMono(evt).filter(tokenTester(idx))
                    .map(tokens -> tokens.getParam(idx))
                    .map(Integer::parseInt)
                    .onErrorResume(NumberFormatException.class, ex -> Mono.just(Integer.MIN_VALUE))
                    .cast(out);
        }
        throw new IllegalArgumentException("@Param must annotate String or OptionalInt value");
    }

    private Predicate<Tokens> tokenTester(int idx) {
        return t -> t.getParams() != null && idx < t.getParams().length;
    }
}
