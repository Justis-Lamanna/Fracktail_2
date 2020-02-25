package com.github.lucbui.calendarfun.token;

import com.github.lucbui.calendarfun.exception.BotException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class TokenizerImpl implements Tokenizer {

    @Value("${discord.prefix}")
    private String prefix;

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
