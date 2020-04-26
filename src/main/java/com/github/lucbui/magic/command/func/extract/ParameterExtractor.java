package com.github.lucbui.magic.command.func.extract;

import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.function.Function;

public interface ParameterExtractor<IN> {
    boolean isValidFor(Parameter parameter);
    <OUT> Function<IN, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out);
}
