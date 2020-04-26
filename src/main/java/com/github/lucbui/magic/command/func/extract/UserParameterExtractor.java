package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.BasicSender;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.function.Function;

public class UserParameterExtractor implements ParameterExtractor<MessageCreateEvent> {
    @Override
    public boolean isValidFor(Parameter parameter) {
        return parameter.isAnnotationPresent(BasicSender.class);
    }

    @Override
    public <OUT> Function<MessageCreateEvent, Mono<OUT>> getExtractorFor(Parameter parameter, Class<OUT> out) {
        if(parameter.getType().equals(User.class)) {
            return evt -> Mono.justOrEmpty(evt.getMessage().getAuthor()).cast(out);
        } else if(parameter.getType().equals(String.class)) {
            return evt -> Mono.justOrEmpty(evt.getMessage().getAuthor()).map(User::getUsername).cast(out);
        }
        throw new IllegalArgumentException("@BasicSender must annotate String or User value");
    }
}
