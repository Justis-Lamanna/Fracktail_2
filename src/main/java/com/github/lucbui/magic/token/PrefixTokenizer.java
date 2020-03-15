package com.github.lucbui.magic.token;

import com.github.lucbui.magic.exception.BotException;

import java.util.regex.Pattern;

/**
 * A basic tokenizer which takes commands using a prefix strategy
 * Format is [prefix][command] [param 1] [param 2]...
 */
public class PrefixTokenizer implements Tokenizer {
    private final String prefix;

    /**
     * Initialize the tokenizer.
     * @param prefix The prefix to use.
     */
    public PrefixTokenizer(String prefix) {
        this.prefix = prefix;
    }

    private static final Pattern SPACE = Pattern.compile("\\s+");

    @Override
    public Tokens tokenize(String message) {
        if(isValid(message)) {
            String[] commandAndParams = SPACE.split(message, 2);
            String commandNoPrefix = commandAndParams[0].substring(prefix.length());
            String paramString = commandAndParams.length < 2 ? null : commandAndParams[1];
            String[] params = paramString == null ? null : SPACE.split(paramString);
            return new Tokens(message, prefix, commandNoPrefix, paramString, params);
        }
        throw new BotException("Attempted to tokenize invalid message");
    }

    @Override
    public boolean isValid(String message) {
        return message.startsWith(prefix);
    }
}
