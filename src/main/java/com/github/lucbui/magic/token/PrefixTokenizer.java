package com.github.lucbui.magic.token;

import com.github.lucbui.magic.exception.BotException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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
    private static final Pattern SPACE_NOT_QUOTES = Pattern.compile("([^\"]\\S*|\".+?(?<!\\\\)\")\\s*");
    private static final Pattern DOUBLE_QUOTES_NO_BACKSLASH = Pattern.compile("(?<!\\\\)\"");

    @Override
    public Tokens tokenize(String message) {
        if(isValid(message)) {
            String[] commandAndParams = SPACE.split(message, 2);
            String commandNoPrefix = commandAndParams[0].substring(prefix.length());
            String paramString = commandAndParams.length < 2 ? null : commandAndParams[1];
            String[] params = paramString == null ? null : parse(paramString);
            return new Tokens(message, prefix, commandNoPrefix, paramString, params);
        }
        throw new BotException("Attempted to tokenize invalid message");
    }

    private String[] parse(String paramString) {
        List<String> matches = new ArrayList<>();
        Matcher m = SPACE_NOT_QUOTES.matcher(paramString);
        while(m.find()) {
            String match = DOUBLE_QUOTES_NO_BACKSLASH.matcher(m.group(1)).replaceAll("");
            matches.add(match);
        }
        return matches.toArray(new String[0]);
    }

    @Override
    public boolean isValid(String message) {
        return message.startsWith(prefix);
    }
}
