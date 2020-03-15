package com.github.lucbui.magic.token;

/**
 * An algorithm which tokenizes a message
 */
public interface Tokenizer {
    /**
     * Break a message into tokens
     * @param message The message to tokenize
     * @return The tokens
     */
    Tokens tokenize(String message);

    /**
     * Test if a message can be tokenized
     * @param message The message to check
     * @return True, if the message can be tokenized
     */
    boolean isValid(String message);
}
