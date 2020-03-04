package com.github.lucbui.magic.command.func;

import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParameterExtractor<IN> {
    private List<Extractor<IN>> extractors;

    private ParameterExtractor(List<Extractor<IN>> extractors) {
        this.extractors = extractors;
    }

    public Function<IN, ?> getExtractor(Parameter input) {
        for(Extractor<IN> extractor : extractors) {
            if(extractor.parameterPredicate.test(input)) {
                return extractor.extractor;
            }
        }
        throw new NoSuchElementException("Expected: " + extractors.stream().map(Extractor::getString).collect(Collectors.joining(",")));
    }

    public static <T> ParameterPredicate<T> ofType(Class<T> clazz) {
        return new ParameterPredicate<T>() {
            @Override
            public boolean test(Parameter parameter) {
                return parameter.getType().equals(clazz);
            }

            @Override
            public String getString() {
                return clazz.toString();
            }
        };
    }

    public static <T> ParameterPredicate<T> ofType(ParameterizedTypeReference<T> ptr) {
        return new ParameterPredicate<T>() {
            @Override
            public boolean test(Parameter parameter) {
                return ptr.getType().equals(parameter.getType());
            }

            @Override
            public String getString() {
                return ptr.getType().toString();
            }
        };
    }

    public static class Builder<IN> {
        private List<Extractor<IN>> extractors;

        public Builder() {
            extractors = new ArrayList<>();
        }

        public <OUT> Builder<IN> with(ParameterPredicate<OUT> predicate, Function<IN, OUT> extractor) {
            extractors.add(new Extractor<>(predicate, extractor));
            return this;
        }

        public ParameterExtractor<IN> build() {
            return new ParameterExtractor<>(extractors);
        }
    }

    private static class Extractor<IN> {
        public ParameterPredicate<?> parameterPredicate;
        public Function<IN, ?> extractor;

        public <OUT> Extractor(ParameterPredicate<OUT> parameterPredicate, Function<IN, OUT> extractor) {
            this.parameterPredicate = parameterPredicate;
            this.extractor = extractor;
        }

        public String getString() {
            return parameterPredicate.getString();
        }
    }

    public interface ParameterPredicate<OUT> {
        boolean test(Parameter parameter);
        String getString();
    }
}
