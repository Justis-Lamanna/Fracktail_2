package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.Params;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.function.Function;

public class ParamsAnnotationParameterExtractor implements ParameterExtractor<MessageCreateEvent> {
    private Tokenizer tokenizer;

    public ParamsAnnotationParameterExtractor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.isAnnotationPresent(Params.class);
    }

    @Override
    public <OUT> Function<MessageCreateEvent, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        if(parameter.getType().equals(String.class)) {
            return evt -> tokenizer.tokenizeToMono(evt).map(Tokens::getParamString).cast(out);
        } else if(parameter.getType().equals(String[].class)) {
            return evt -> tokenizer.tokenizeToMono(evt).map(Tokens::getParams).cast(out);
        }
        throw new IllegalArgumentException("@Params must annotate String or String[] value");
    }
}
