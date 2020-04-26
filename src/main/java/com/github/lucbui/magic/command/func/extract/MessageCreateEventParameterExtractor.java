package com.github.lucbui.magic.command.func.extract;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.function.Function;

public class MessageCreateEventParameterExtractor implements ParameterExtractor<MessageCreateEvent> {

    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.getType().equals(MessageCreateEvent.class);
    }

    @Override
    public <OUT> Function<MessageCreateEvent, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        return evt -> Mono.just(evt).cast(out);
    }
}
