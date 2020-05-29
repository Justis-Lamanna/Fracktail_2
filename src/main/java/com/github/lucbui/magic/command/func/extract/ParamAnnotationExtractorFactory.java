package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.Default;
import com.github.lucbui.magic.annotation.Param;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class ParamAnnotationExtractorFactory implements ExtractorFactory {
    private Tokenizer tokenizer;

    public ParamAnnotationExtractorFactory(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Extractor getExtractorFor(Parameter parameter) {
        int idx = parameter.getAnnotation(Param.class).value();
        if (idx < 0) {
            throw new IllegalArgumentException("@Param value must be non-negative");
        }

        if(parameter.getType().equals(String.class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx).filter(tokenTester(idx))
                    .map(tokens -> tokens.getParam(idx))
                    .switchIfEmpty(getDefaultValue(parameter))
                    .cast(Object.class);
        } else if(parameter.getType().equals(OptionalInt.class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx).filter(tokenTester(idx))
                    .map(tokens -> tokens.getParam(idx))
                    .map(Integer::parseInt)
                    .map(OptionalInt::of)
                    .onErrorResume(NumberFormatException.class, ex -> Mono.empty())
                    .switchIfEmpty(getOptionalIntDefault(parameter))
                    .cast(Object.class);
        } else if(parameter.getType().equals(Integer.TYPE)) {
            return ctx -> tokenizer.tokenizeToMono(ctx).filter(tokenTester(idx))
                    .map(tokens -> tokens.getParam(idx))
                    .map(Integer::parseInt)
                    .onErrorResume(NumberFormatException.class, ex -> Mono.empty())
                    .switchIfEmpty(getIntegerDefault(parameter))
                    .cast(Object.class);
        }
        throw new IllegalArgumentException("@Param must annotate String or OptionalInt value");
    }

    private Mono<String> getDefaultValue(Parameter parameter) {
        if(parameter.isAnnotationPresent(Default.class)) {
            return Mono.just(parameter.getAnnotation(Default.class).value());
        }
        return Mono.empty();
    }

    private Mono<Integer> getIntegerDefault(Parameter parameter) {
        return getDefaultValue(parameter)
                .map(Integer::parseInt)
                .onErrorResume(NumberFormatException.class, ex -> Mono.just(0));
    }

    private Mono<OptionalInt> getOptionalIntDefault(Parameter parameter) {
        return getDefaultValue(parameter)
                .map(Integer::parseInt)
                .map(OptionalInt::of)
                .onErrorResume(NumberFormatException.class, ex -> Mono.just(OptionalInt.empty()));
    }

    private Predicate<Tokens> tokenTester(int idx) {
        return t -> t.getParams() != null && idx < t.getParams().length;
    }
}
