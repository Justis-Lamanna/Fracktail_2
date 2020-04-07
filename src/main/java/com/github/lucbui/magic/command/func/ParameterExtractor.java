package com.github.lucbui.magic.command.func;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper class for extracting parameters for annotated commands
 * @param <IN> The input type.
 */
public class ParameterExtractor<IN> {
    private List<Extractor<IN>> extractors;

    private ParameterExtractor(List<Extractor<IN>> extractors) {
        this.extractors = extractors;
    }

    /**
     * Get an extractor for the input parameter
     * @param input The input parameter
     * @return A function which converts the input type into a type compatable with the passed Parameter
     */
    public Function<IN, ?> getExtractor(Parameter input) {
        for(Extractor<IN> extractor : extractors) {
            if(extractor.parameterPredicate.test(input)) {
                return extractor.extractor;
            }
        }
        throw new NoSuchElementException("Expected: " + extractors.stream().map(Extractor::getString).collect(Collectors.joining(",")));
    }

    /**
     * A ParameterPredicate which matches on class
     * @param clazz The class to match
     * @param <T> The class to match
     * @return A ParameterPredicate which matches if the Parameter class equals this class
     */
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

    /**
     * A builder which generates ParameterExtractors
     * @param <IN> The input type
     */
    public static class Builder<IN> {
        private List<Extractor<IN>> extractors;

        /**
         * Initialize this builder
         */
        public Builder() {
            extractors = new ArrayList<>();
        }

        /**
         * Add a ParameterPredicate - Function mapping
         * @param predicate The predicate to use
         * @param extractor The extractor to map against
         * @param <OUT> The output type
         * @return This builder
         */
        public <OUT> Builder<IN> with(ParameterPredicate<OUT> predicate, Function<IN, OUT> extractor) {
            extractors.add(new Extractor<>(predicate, extractor));
            return this;
        }

        /**
         * Build the ParameterExtractor
         * @return The created ParameterExtractor
         */
        public ParameterExtractor<IN> build() {
            return new ParameterExtractor<>(extractors);
        }
    }

    /**
     * Describes a mapping from ParameterPredicate to Function
     * @param <IN> The input type
     */
    private static class Extractor<IN> {
        public ParameterPredicate<?> parameterPredicate;
        public Function<IN, ?> extractor;

        /**
         * Create an Extractor
         * @param parameterPredicate A predicate which matches the incoming Parameter object
         * @param extractor A function which extracts an object compatable with the input Parameter object
         * @param <OUT> The output type
         */
        public <OUT> Extractor(ParameterPredicate<OUT> parameterPredicate, Function<IN, OUT> extractor) {
            this.parameterPredicate = parameterPredicate;
            this.extractor = extractor;
        }

        /**
         * Get the string representation of this Extractor
         * @return the string representation of this Extractor
         */
        public String getString() {
            return parameterPredicate.getString();
        }
    }

    /**
     * An interface which describes a Parameter predicate
     * @param <OUT> The output type
     */
    public interface ParameterPredicate<OUT> {
        /**
         * Test if the input Parameter matches
         * @param parameter The input parameter
         * @return True if matching
         */
        boolean test(Parameter parameter);

        /**
         * Get the string representation of this Extractor
         * @return the string representation of this Extractor
         */
        String getString();
    }
}
