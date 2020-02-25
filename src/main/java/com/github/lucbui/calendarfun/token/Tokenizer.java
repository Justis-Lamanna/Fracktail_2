package com.github.lucbui.calendarfun.token;

public interface Tokenizer {
    Tokens tokenize(String message);

    boolean isValid(String message);
}
