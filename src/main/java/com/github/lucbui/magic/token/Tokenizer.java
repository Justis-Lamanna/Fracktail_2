package com.github.lucbui.magic.token;

public interface Tokenizer {
    Tokens tokenize(String message);

    boolean isValid(String message);
}
