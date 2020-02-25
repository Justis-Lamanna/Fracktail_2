package com.github.lucbui.calendarfun.util;

import org.springframework.core.ParameterizedTypeReference;

import java.util.Optional;
import java.util.stream.Stream;

public class OptionalUtils {
    public static final ParameterizedTypeReference<Optional<String>> PTR_OPTIONAL_STRING =
            new ParameterizedTypeReference<Optional<String>>() {};

    public static final ParameterizedTypeReference<Optional<String[]>> PTR_OPTIONAL_STRING_ARRAY =
            new ParameterizedTypeReference<Optional<String[]>>() {};

    public static <T> Stream<T> fromOptional(Optional<T> opt) {
        return opt.map(Stream::of).orElse(Stream.empty());
    }
}
